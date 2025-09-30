package com.example.tridots.repository;

import com.example.tridots.model.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, String> {
    List<Mensagem> findByPedidoIdPedidoOrderByDataHora(String pedidoId);
    Page<Mensagem> findByPedidoIdPedidoOrderByDataHora(String pedidoId, Pageable pageable);
}
