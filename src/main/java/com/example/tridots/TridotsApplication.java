package com.example.tridots;

import com.example.tridots.enums.Cargo;
import com.example.tridots.model.Administrador;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.example.tridots.enums.Cargo.ADMINISTRADOR;

@SpringBootApplication
public class TridotsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TridotsApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode("senha123");
		System.out.println("porra da print: " + hashedPassword);

	}

	@Bean
	public CommandLineRunner criarAdminPadrao(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
		return args -> {
			if (usuarioRepository.findByEmailInstitucional("admin@unisale.edu.br") == null) {

				Administrador admin = new Administrador();
				admin.setNome("Administrador Padrão");
				admin.setEmailInstitucional("admin@unisale.edu.br");
				admin.setPassword(encoder.encode("admin123"));
				admin.setCargo(Cargo.ADMINISTRADOR);
				admin.setEmail("admin@unisale.edu.br");
				admin.setTelefone("11999999999");

				usuarioRepository.save(admin);

				System.out.println("\n✅ ADMIN criado automaticamente!");
				System.out.println("   email: admin@unisale.edu.br");
				System.out.println("   senha: admin123\n");
			} else {
				System.out.println("\n✔️ Admin já existe (nenhuma ação necessária).\n");
			}
		};
	}



}
