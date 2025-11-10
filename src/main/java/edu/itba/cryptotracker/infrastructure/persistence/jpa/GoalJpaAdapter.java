package edu.itba.cryptotracker.adapter.output.persistence.jpa;

import edu.itba.cryptotracker.adapter.output.persistence.jpa.mapper.GoalJpaMapper;
import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.persistence.GoalRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class GoalJpaAdapter implements GoalRepositoryPort {

    private final GoalJpaRepository jpaRepository;
    private final GoalJpaMapper entityMapper;

    @Override
    public Goal save(Goal goal) {
        var entity = entityMapper.toEntity(goal);
        var saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<Goal> findById(String id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public Optional<Goal> findByCryptoId(String cryptoId) {
        return jpaRepository.findByCryptoId(cryptoId).map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByCryptoId(String cryptoId) {
        return jpaRepository.existsByCrypto_Id(cryptoId);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public int countGoals() {
        return (int) jpaRepository.count();
    }

    @Override
    public List<Goal> findAll() {
        return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }

}
