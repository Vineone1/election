package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final VoterClientService voterClientService;

    @Autowired
    public VoteService(VoteRepository voteRepository, ElectionRepository electionRepository, VoterClientService voterClientService){
        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
        this.voterClientService = voterClientService;
    }

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

    public GenericOutput electionVote(VoteInput voteInput){

        Election election = validateInput(voteInput.getElectionId(), voteInput);
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setVoterId(voteInput.getVoterId());

        if (voteInput.getCandidateNumber() == null){
            vote.setBlankVote(true);
        } else {
            vote.setBlankVote(false);
        }

        // TODO: Validate null candidate
        vote.setNullVote(false);
        voteRepository.save(vote);
        return new GenericOutput("OK - aqui tem problema");
    }

    public GenericOutput multiple(List<VoteInput> voteInputList){
        for (VoteInput voteInput : voteInputList){
            this.electionVote(voteInput);
        }
        return new GenericOutput("OK");
    }

    public Election validateInput(Long electionId, VoteInput voteInput){
        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException("Invalid Election");
        }
        if (voteInput.getVoterId() == null){
            throw new GenericOutputException("Invalid Voter");
        }
        // TODO: Validate voter
        VoterOutput voterOutput = null;
        try{
            voterOutput = voterClientService.getById(voteInput.getVoterId());
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException("Errou Errou");
            }
        }
        // VALIDANDO SE O ELEITOR JA VOTOU NESTA ELEIÇAO
        Vote vote = voteRepository.findFirstByVoterIdAndElection(voterOutput.getId(), election);
        if (vote != null){
            throw new GenericOutputException("Eleitor não pode votar duas vezes na mesma eleição");
        }
        return election;
    }

}
