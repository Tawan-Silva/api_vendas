package br.com.tawandev.vendas.rest.controller;

import br.com.tawandev.vendas.domain.entities.Usuario;
import br.com.tawandev.vendas.exception.SenhaInvalidaException;
import br.com.tawandev.vendas.rest.dto.CredenciasDTO;
import br.com.tawandev.vendas.rest.dto.TokenDTO;
import br.com.tawandev.vendas.security.jtw.JwtService;
import br.com.tawandev.vendas.services.impl.UsuarioServiceImpl;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
@Api("Api Usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo usuário")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuário salvo com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
    })
    private Usuario salvar(@RequestBody @Valid Usuario usuario) {
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return usuarioService.salvar(usuario);
    }

    @PostMapping("/auth")
    @ApiOperation("Login de usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login feito com sucesso"),
            @ApiResponse(code = 403, message = "Erro ao tentar fazer login"),
    })
    public TokenDTO autenticar(@RequestBody @ApiParam("Login e senha do usuário") CredenciasDTO credencias) {
        try {
          Usuario usuario = Usuario.builder()
                    .login(credencias.getLogin())
                    .senha(credencias.getSenha()).build();

            usuarioService.autenticar(usuario);
            String token = jwtService.gerarToken(usuario);

            return new TokenDTO(usuario.getLogin(), token);
        }
        catch (UsernameNotFoundException | SenhaInvalidaException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }

    }
}
