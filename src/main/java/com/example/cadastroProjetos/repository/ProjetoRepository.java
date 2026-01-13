package com.example.cadastroProjetos.repository;
import com.example.cadastroProjetos.model.entity.ProjetoEntity;
import com.example.cadastroProjetos.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<ProjetoEntity, Long> {
    @Query("""
            SELECT COUNT(p)
            FROM projeto p
            JOIN p.membrosIds m
            WHERE m = :membroId
            AND p.status NOT IN (:statusEncerrados)
            """)
    long contarProjetosMembroAtivo(
            @Param("membroId") Long membroId,
            @Param("statusEncerrados") List<Status> statusEncerrados
    );

}
