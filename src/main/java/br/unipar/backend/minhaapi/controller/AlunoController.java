package br.unipar.backend.minhaapi.controller;

import br.unipar.backend.minhaapi.model.Aluno;
import br.unipar.backend.minhaapi.repository.AlunoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoRepository alunoRepository;

    public AlunoController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Aluno>> listarAlunos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String cidade) {
        List<Aluno> resultado = new ArrayList<>();

        for (Aluno aluno : alunoRepository.findAll()) {
            // O aluno precisa atender a todos os filtros informados.
            if (corresponde(aluno.getNome(), nome)
                    && corresponde(aluno.getCurso(), curso)
                    && corresponde(aluno.getCidade(), cidade)) {
                resultado.add(aluno);
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarAluno(@PathVariable int id) {

        return alunoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Aluno> cadastrarAluno(@RequestBody Aluno aluno) {
        // O banco gera o ID; qualquer ID enviado pelo cliente é ignorado.
        aluno.setId(null);
        Aluno salvo = alunoRepository.save(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizarAluno(@PathVariable int id,
                                               @RequestBody Aluno novosDados) {
        Optional<Aluno> alunoEncontrado = alunoRepository.findById(id);
        if (alunoEncontrado.isPresent()) {
            Aluno aluno = alunoEncontrado.get();
            aluno.setNome(novosDados.getNome());
            aluno.setIdade(novosDados.getIdade());
            aluno.setCurso(novosDados.getCurso());
            aluno.setCidade(novosDados.getCidade());
            return ResponseEntity.ok(alunoRepository.save(aluno));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirAluno(@PathVariable int id) {
        if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
            return ResponseEntity.ok("Aluno " + id + " excluido com sucesso!");
        }
        return ResponseEntity.notFound().build();
    }

    private boolean corresponde(String valor, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        if (valor == null) {
            return false;
        }
        return normalizar(valor).contains(normalizar(filtro));
    }

    private String normalizar(String texto) {
        // Separa e remove os acentos para que "Joao" encontre "João".
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
