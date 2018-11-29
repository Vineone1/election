package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CandidateClientService {

    private final CandidateClient candidateClient;

    @Autowired
    public CandidateClientService(CandidateClient candidateClient) {
        this.candidateClient = candidateClient;
    }

    public CandidateOutput getById(Long id){
        return this.candidateClient.getById(id);
    }

    //RETORNA TRUE SE TEM CANDIDATO NA ELEICAO
    public Boolean getByElection(Long id){
        return this.candidateClient.getByElection(id);
    }

    @FeignClient(value="candidate-service", url="${url.candidate-service}")
    private interface CandidateClient {

        @GetMapping("/v1/candidate/{candidateId}")
        CandidateOutput getById(@PathVariable(name = "candidateId") Long electionId);

        @GetMapping("/v1/candidate/byElection/{electionId}")
        Boolean getByElection (@PathVariable(name="electionId") Long electionId);
    }
}
