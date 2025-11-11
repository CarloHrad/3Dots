package com.example.tridots.AlunoControllerTest;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Alunos.AlterarSenhaDTO;
import com.example.tridots.dto.Alunos.AlunoRegisterDTO;
import com.example.tridots.dto.Alunos.AlunoUpdateDTO;
import com.example.tridots.model.Aluno;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.AlunoRepository;
import com.example.tridots.repository.UsuarioRepository;
import com.example.tridots.security.TokenService;
import com.example.tridots.service.AlunoService;
import com.example.tridots.service.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AlunoService alunoService;

    private Aluno alunoExistente;

    @BeforeEach
    void setup() {
        alunoExistente = new Aluno();
        alunoExistente.setNome("João Silva");
        alunoExistente.setEmailInstitucional("joao@faculdade.edu.br");
        alunoExistente.setPassword("12345");
        alunoExistente.setRaAluno("202312345");
        alunoExistente.setCurso("Sistemas de Informação");
        alunoExistente.setSemestre(3);
        usuarioRepository.save(alunoExistente);

        var auth = new UsernamePasswordAuthenticationToken(alunoExistente, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ===============================================================
    // ======================= REGISTER TESTS ========================
    // ===============================================================

    @Test
    @DisplayName("Deve retornar SUCCESS quando o aluno for cadastrado com sucesso")
    void registerAluno_Success() throws Exception {
        AlunoRegisterDTO dto = new AlunoRegisterDTO(
                "Maria Oliveira",
                "maria@faculdade.edu.br",
                "senha123",
                "202312346",
                "Engenharia de Software",
                2
        );


        BaseResponse response = alunoService.createAluno(dto);

        assertEquals(OperationCode.SUCCESSFUL_Operation.getCode(), response.getOperationCode());
        assertEquals(OperationCode.SUCCESSFUL_Operation.getCode(), response.getOperationCode());
        assertEquals(OperationCode.SUCCESSFUL_Operation.getCode(), response.getOperationCode());

        String idAluno = usuarioRepository.findByEmailInstitucional(dto.emailInstitucional()).getIdUsuario();
        Optional<Usuario> aluno = usuarioRepository.findById(idAluno);

        assertEquals("Maria Oliveira", aluno.get().getNome());
        assertEquals("maria@faculdade.edu.br", aluno.get().getEmailInstitucional());
    }

    @Test
    @DisplayName("Deve retornar FAILURE quando o email já estiver cadastrado")
    void registerAluno_EmailAlreadyExists() throws Exception {
        AlunoRegisterDTO dto = new AlunoRegisterDTO(
                "José da Silva",
                alunoExistente.getEmailInstitucional(),
                "senha123",
                "202312347",
                "Engenharia de Software",
                1
        );

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.EMAIL_AlreadyExists.getCode()));
    }

    @Test
    @DisplayName("Deve retornar FAILURE quando o RA já estiver cadastrado")
    void registerAluno_RaAlreadyExists() throws Exception {
        AlunoRegisterDTO dto = new AlunoRegisterDTO(
                "Pedro Henrique",
                "pedro@faculdade.edu.br",
                "senha123",
                alunoExistente.getRaAluno(),
                "Sistemas de Informação",
                1
        );

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.RA_AlreadyExists.getCode()));
    }

    @Test
    @DisplayName("Deve retornar FAILURE quando o nome for nulo")
    void registerAluno_NomeIsNull_returnsFailure() throws Exception {
        AlunoRegisterDTO dto = new AlunoRegisterDTO(
                null,
                "novo@faculdade.edu.br",
                "senha123",
                "202312400",
                "Engenharia",
                1
        );

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.ARGUMENT_NullOrEmpty.getCode()));
    }

    // ===============================================================
    // ========================= LOGIN TESTS =========================
    // ===============================================================

    @Test
    @DisplayName("Deve retornar SUCCESS quando o login for realizado com sucesso")
    void loginAluno_Success() throws Exception {
        AlunoRegisterDTO dto = new AlunoRegisterDTO(
                "Lucas Souza",
                "lucas@faculdade.edu.br",
                "senha123",
                "202312355",
                "ADS",
                2
        );
        alunoService.createAluno(dto);

        var login = """
                {
                  "emailInstitucional": "lucas@faculdade.edu.br",
                  "password": "senha123"
                }
                """;

        mockMvc.perform(post("/aluno/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.SUCCESSFUL_Operation.getCode()));
    }

    @Test
    @DisplayName("Deve retornar FAILURE quando as credenciais forem inválidas")
    void loginAluno_InvalidCredentials() throws Exception {
        var login = """
                {
                  "emailInstitucional": "joao@faculdade.edu.br",
                  "password": "senhaErrada"
                }
                """;

        mockMvc.perform(post("/aluno/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.LOGIN_Invalid.getCode()));
    }

    // ===============================================================
    // ========================= UPDATE TESTS ========================
    // ===============================================================

    @Test
    @DisplayName("Deve retornar SUCCESS ao atualizar informações válidas")
    void updateAluno_Success() throws Exception {
        AlunoUpdateDTO dto = new AlunoUpdateDTO("João Atualizado", 4);

        mockMvc.perform(put("/aluno/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.SUCCESSFUL_Operation.getCode()));
    }

    @Test
    @DisplayName("Deve retornar FAILURE quando nome for vazio")
    void updateAluno_NomeIsEmpty_returnsFailure() throws Exception {
        AlunoUpdateDTO dto = new AlunoUpdateDTO(" ", 4);

        mockMvc.perform(put("/aluno/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.ARGUMENT_NullOrEmpty.getCode()));
    }

    // ===============================================================
    // ====================== ALTERAR SENHA TESTS ====================
    // ===============================================================

    @Test
    @DisplayName("Deve retornar FAILURE quando nova senha for igual à atual")
    void alterarSenha_ReuseNotAllowed_returnsFailure() throws Exception {
        AlterarSenhaDTO dto = new AlterarSenhaDTO("12345", "12345");

        mockMvc.perform(put("/aluno/" + alunoExistente.getIdUsuario() + "/alterar-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.PASSWORD_ReuseNotAllowed.getCode()));
    }

    @Test
    @DisplayName("Deve retornar ACESSO NEGADO se falhar ao tentar alterar a senha seme star cadastrado")
    void alterarSenha_InternalError_returnsFailure() throws Exception {
        AlterarSenhaDTO dto = new AlterarSenhaDTO("senhaAntiga", "senhaNova");

        mockMvc.perform(put("/aluno/999/alterar-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.operationCode")
                        .value(OperationCode.ACCESS_Denid.getCode()));
    }
}

