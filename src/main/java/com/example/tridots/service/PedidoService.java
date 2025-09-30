package com.example.tridots.service;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.*;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Alunos.AlunoDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
import com.example.tridots.dto.Pedidos.PedidoRequestDTO;
import com.example.tridots.dto.Pedidos.PedidoResponseDTO;
import com.example.tridots.enums.StatusPedido;
import com.example.tridots.model.*;
import com.example.tridots.repository.AlunoRepository;
import com.example.tridots.repository.ArquivoRepository;
import com.example.tridots.repository.PedidoRepository;
import com.example.tridots.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ArquivoRepository arquivoRepository;
    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    ArquivoService arquivoService;

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    @PreAuthorize("hasRole('ALUNO')")
    public BaseResponse criarPedido(String idAluno, MultipartFile file, PedidoRequestDTO dto) throws IOException {

        Aluno userAluno = alunoRepository.findById(idAluno).orElseThrow(() -> {
            log.error("Aluno com id {} não encontrado!", idAluno);
            return new RuntimeException("Aluno não encontrado!");
        });

        Arquivo arquivo = arquivoService.salvar(file);
        log.info("Arquivo salvo via CRIAR PEDIDO!");

        Pedido pedido = new Pedido();
        pedido.setAluno(userAluno);
        pedido.setArquivo(arquivo);
        pedido.setDescricao(dto.descricao());
        pedido.setMedidas(dto.medidas());
        pedido.setObservacao(dto.observacao());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setData(LocalDateTime.now());

        Pedido saved = pedidoRepository.save(pedido);
        log.info("Pedido salvo!");

        PedidoResponseDTO pedidoDTO = new PedidoResponseDTO(
                pedido.getIdPedido(),
                new ArquivoDTO(arquivo.getIdArquivo(), arquivo.getNomeArquivo(), arquivo.getTipoArquivo()),
                pedido.getDescricao(),
                pedido.getMedidas(),
                pedido.getObservacao(),
                pedido.getDiasEstimados(),
                pedido.getData(),
                pedido.getStatus().name()
        );

        return new BaseResponse(
                OperationCode.SUCCESSFUL_Operation.getCode(),
                OperationCode.SUCCESSFUL_Operation.getDescription(),
                pedidoDTO,
                OperationCode.SUCCESSFUL_Operation.getHttpStatus()
        );
    }

    @PreAuthorize("hasRole('ALUNO')")
    public BaseResponse cancelarPedido(String idAluno, String idPedido) throws AccessDeniedException {

        Aluno userAluno = alunoRepository.findById(idAluno).orElse(null);
        if (userAluno == null) {
            return new BaseResponse(
                    OperationCode.LOGIN_NotFound.getCode(),
                    OperationCode.LOGIN_NotFound.getDescription(),
                    null,
                    OperationCode.LOGIN_NotFound.getHttpStatus()
            );
        }

        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null) {
            return new BaseResponse(
                    OperationCode.PEDIDO_NotFound.getCode(),
                    OperationCode.PEDIDO_NotFound.getDescription(),
                    null,
                    OperationCode.PEDIDO_NotFound.getHttpStatus()
            );
        }

        if (!pedido.getAluno().getIdUsuario().equals(idAluno)) {
            return new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
        }

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            System.out.println("Erro: Apenas pedidos em estado 'PENDENTE' ou podem ser cancelados...");
            return new BaseResponse(
                    OperationCode.PEDIDO_InvalidStatusForAction.getCode(),
                    OperationCode.PEDIDO_InvalidStatusForAction.getDescription() + ": Apenas pedidos em estado 'PENDENTE' ou podem ser cancelados.",
                    null,
                    OperationCode.PEDIDO_InvalidStatusForAction.getHttpStatus()
            );
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

        return new BaseResponse(
                OperationCode.SUCCESSFUL_Operation.getCode(),
                OperationCode.SUCCESSFUL_Operation.getDescription() + ": Pedido cancelado com sucesso",
                null,
                OperationCode.SUCCESSFUL_Operation.getHttpStatus()
        );

    }

    //@PostMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public BaseResponse atualizarStatus(String idPedido, StatusUpdateDTO dto) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null) {
            return new BaseResponse(
                    OperationCode.PEDIDO_NotFound.getCode(),
                    OperationCode.PEDIDO_NotFound.getDescription(),
                    null,
                    OperationCode.PEDIDO_NotFound.getHttpStatus()
            );
        }

        Arquivo arquivo = pedido.getArquivo();

        pedido.setStatus(dto.status());
        pedido.setDiasEstimados(dto.diasEstimados());
        pedidoRepository.save(pedido);

        PedidoResponseDTO pedidoDTO = new PedidoResponseDTO(
                pedido.getIdPedido(),
                new ArquivoDTO(arquivo.getIdArquivo(), arquivo.getNomeArquivo(), arquivo.getTipoArquivo()),
                pedido.getDescricao(),
                pedido.getMedidas(),
                pedido.getObservacao(),
                pedido.getDiasEstimados(),
                pedido.getData(),
                pedido.getStatus().name()
        );

        return new BaseResponse(
                OperationCode.SUCCESSFUL_Operation.getCode(),
                OperationCode.SUCCESSFUL_Operation.getDescription(),
                pedidoDTO,
                OperationCode.SUCCESSFUL_Operation.getHttpStatus()
        );
    }

    @PreAuthorize("hasRole('ALUNO')")
    public List<PedidoResponseDTO> listarMeusPedidos(String idAluno) {
        return pedidoRepository.findByAluno_IdUsuario(idAluno)
                .stream()
                .map(pedido -> {
                    PedidoResponseDTO dto = new PedidoResponseDTO(
                            pedido.getIdPedido(),
                            new ArquivoDTO(pedido.getArquivo().getIdArquivo(), pedido.getArquivo().getNomeArquivo(), pedido.getArquivo().getTipoArquivo()),
                            pedido.getDescricao(),
                            pedido.getMedidas(),
                            pedido.getObservacao(),
                            pedido.getDiasEstimados(),
                            pedido.getData(),
                            pedido.getStatus().name()
                    );
                    return dto;
                }).toList();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<PedidoAlunoDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedido -> new PedidoAlunoDTO(
                        new AlunoDTO(
                                pedido.getAluno().getNome(),
                                pedido.getAluno().getEmailInstitucional(),
                                pedido.getAluno().getRaAluno(),
                                pedido.getAluno().getCurso(),
                                pedido.getAluno().getSemestre()
                        ),
                        pedido.getIdPedido(),
                        new ArquivoDTO(
                                pedido.getArquivo().getIdArquivo(),
                                pedido.getArquivo().getNomeArquivo(),
                                pedido.getArquivo().getTipoArquivo()
                        ),
                        pedido.getDescricao(),
                        pedido.getMedidas(),
                        pedido.getObservacao(),
                        pedido.getDiasEstimados(),
                        pedido.getData(),
                        pedido.getStatus().name()
                ))
                .toList();
    }
}

    /*public PedidoResponseDTO atualizarPedido(String idUsuario, PedidoRequestDTO pedidoRequestDTO) {

    }*/

    /*public PedidoResponseDTO buscarPorId(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return PedidoResponseDTO.fromEntity(pedido);
    }





    }*/

