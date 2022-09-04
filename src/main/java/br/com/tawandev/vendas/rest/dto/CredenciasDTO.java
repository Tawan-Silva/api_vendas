package br.com.tawandev.vendas.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CredenciasDTO {

    private String login;
    private String senha;
}
