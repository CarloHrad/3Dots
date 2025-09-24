package com.example.tridots.repository;

import com.example.tridots.model.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository<Arquivo, String> {
}
