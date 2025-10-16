package com.example.tridots.model;

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
    private Double altura;
    private Double largura;
    private Double profundidade;

    public Double calcularVolume() {
        if (altura == null || largura == null || profundidade == null) {
            return null;
        }
        return altura * largura * profundidade;
    }
}
