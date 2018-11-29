package br.edu.ulbra.election.election.repository;

import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, Long> {
    Vote findFirstByVoterIdAndElection(Long voterId, Election election);
    Boolean existsVotesByElection_Id(Long electionId);
    Boolean existsVotesByElection_IdAndCandidateId(Long electionId, Long candidateId);
}
