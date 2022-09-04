package br.com.tawandev.vendas.rest.controller;

import br.com.tawandev.vendas.domain.entities.Cliente;
import br.com.tawandev.vendas.repositories.ClientesRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/clientes")
@RestController
@Api("Api Clientes")
public class ClienteController {

    public static final String CLIENTE_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO = "Cliente não encontrado para o ID informado";
    public static final String ERRO_DE_VALIDAÇÃO = "Erro de validação";
    @Autowired
    private ClientesRepository clientesRepository;

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de um Cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = CLIENTE_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO)
    })
    public Cliente getClienteById(@PathVariable @ApiParam("Id do cliente") Integer id) {
        return clientesRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo Cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente salvo com sucesso"),
            @ApiResponse(code = 400, message = ERRO_DE_VALIDAÇÃO)
    })
    public ResponseEntity save(@RequestBody @Valid Cliente cliente) {
        Cliente obj = clientesRepository.save(cliente);
        return ResponseEntity.ok(obj);
    }


    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Atualiza um Cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente atualizado com sucesso"),
            @ApiResponse(code = 400, message = ERRO_DE_VALIDAÇÃO),
            @ApiResponse(code = 404, message = CLIENTE_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO)
    })
    public void update(@PathVariable @ApiParam("Id do cliente") Integer id, @RequestBody @Valid Cliente cliente) {
         clientesRepository.findById(id)
                .map(clienteExistente -> {
                    cliente.setId(clienteExistente.getId());
                    clientesRepository.save(cliente);
            return clienteExistente;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deleta um Cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente deletado com sucesso"),
            @ApiResponse(code = 404, message = CLIENTE_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO)
    })
    public void delete(@PathVariable @ApiParam("Id do cliente") Integer id) {
       clientesRepository.findById(id)
                .map(cliente -> {
                    clientesRepository.delete(cliente);
                    return cliente;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @GetMapping
    @ApiOperation("Retorna um Cliente ou lista de Clientes filtrando por parametro.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente(s) encontrado"),
    })
    public List<Cliente> find(Cliente filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);

        Example example = Example.of(filtro, matcher);
        return clientesRepository.findAll(example);
    }
}
