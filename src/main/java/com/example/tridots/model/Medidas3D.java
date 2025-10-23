package com.example.tridots.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medidas3D {
    @Column(name = "x", nullable = false)
    private Double altura;
    @Column(name = "y", nullable = false)
    private Double largura;
    @Column(name = "z", nullable = false)
    private Double profundidade;

    public Double calcularVolume() {
        if (altura == null || largura == null || profundidade == null) {
            return null;
        }
        return altura * largura * profundidade;
    }
}
