package br.unipar.backend.minhaapi.repository;

import br.unipar.backend.minhaapi.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
}
