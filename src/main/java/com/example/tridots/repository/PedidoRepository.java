package com.example.tridots.repository;

import com.example.tridots.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, String> {
    List<Pedido> findByAluno_IdUsuario(String idAluno);
}
