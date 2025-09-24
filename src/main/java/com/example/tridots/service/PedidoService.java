package com.example.tridots.service;

import com.example.tridots.controller.AlunoController;
import com.example.tridots.dto.*;
import com.example.tridots.enums.StatusPedido;
import com.example.tridots.model.*;
import com.example.tridots.repository.AlunoRepository;
import com.example.tridots.repository.ArquivoRepository;
import com.example.tridots.repository.PedidoRepository;
import com.example.tridots.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public PedidoResponseDTO criarPedido(String idAluno, MultipartFile file, PedidoRequestDTO dto) throws IOException {

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

        return new PedidoResponseDTO(
                pedido.getIdPedido(),
                new ArquivoDTO(arquivo.getIdArquivo(), arquivo.getNomeArquivo(), arquivo.getTipoArquivo()),
                pedido.getDescricao(),
                pedido.getMedidas(),
                pedido.getObservacao(),
                pedido.getDiasEstimados(),
                pedido.getData(),
                pedido.getStatus().name()
        );
    }

    public void cancelarPedido(String idAluno, String idPedido) throws AccessDeniedException {
        Aluno userAluno = alunoRepository.findById(idAluno).orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!pedido.getAluno().getIdUsuario().equals(idAluno)) {
            throw new AccessDeniedException("Você só consegue cancelar pedidos feitos por você mesmo");
        }

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            System.out.println("Erro: Apenas pedidos em estado 'PENDENTE' ou podem ser cancelados...");
            throw new RuntimeException("Falha em cancelar pedido");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

    }

    //@PreAuthorize("hasRole('ADMIN')")
    //@PostMapping("/pedido/{id}/status")
    public PedidoResponseDTO atualizarStatus(String idPedido, StatusUpdateDTO dto) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Arquivo arquivo = pedido.getArquivo();

        pedido.setStatus(dto.status());
        pedido.setDiasEstimados(dto.diasEstimados());
        pedidoRepository.save(pedido);

        return new PedidoResponseDTO(
                pedido.getIdPedido(),
                new ArquivoDTO(arquivo.getIdArquivo(), arquivo.getNomeArquivo(), arquivo.getTipoArquivo()),
                pedido.getDescricao(),
                pedido.getMedidas(),
                pedido.getObservacao(),
                pedido.getDiasEstimados(),
                pedido.getData(),
                pedido.getStatus().name()
        );
    }

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
                        pedido.getArquivo().getIdArquivo(),
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

