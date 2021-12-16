package edu.iu.uits.lms.iuonly.services.rest;

import edu.iu.uits.lms.iuonly.model.LmsBatchEmail;
import edu.iu.uits.lms.iuonly.repository.LmsBatchEmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yingwang on 10/29/15.
 */
@RestController
@RequestMapping("/rest/iu/batchemail")
@Slf4j
public class BatchEmailRestController extends BaseService {

    @Autowired
    private LmsBatchEmailRepository lmsBatchEmailRepository;

    @GetMapping(value = "/{id}")
    public LmsBatchEmail getBatchEmailFromId(@PathVariable Long id) {
        return lmsBatchEmailRepository.findById(id).orElse(null);
    }

    @GetMapping(value = "/groupcode/{groupcode}")
    public LmsBatchEmail getBatchEmailFromGroupCode(@PathVariable String groupcode) {
        return lmsBatchEmailRepository.getBatchEmailFromGroupCode(groupcode);
    }

    @GetMapping(value = "/all")
    public List<LmsBatchEmail> getBatchEmailAll() {
        List<LmsBatchEmail> batchEmails = (List<LmsBatchEmail>) lmsBatchEmailRepository.findAll();
        return batchEmails;
    }

    @PutMapping(value = "/{id}")
    public LmsBatchEmail updateBatchEmail(@PathVariable Long id, @RequestBody String emails) {
        LmsBatchEmail updatedBatchEmail = lmsBatchEmailRepository.findById(id).orElse(null);
        updatedBatchEmail.setEmails(emails);
        return lmsBatchEmailRepository.save(updatedBatchEmail);
    }

    @PostMapping("/")
    public LmsBatchEmail createBatchEmail(@RequestBody LmsBatchEmail lmsBatchEmail) {
        LmsBatchEmail newBatchEmail = new LmsBatchEmail();
        newBatchEmail.setGroupCode(lmsBatchEmail.getGroupCode());
        newBatchEmail.setEmails(lmsBatchEmail.getEmails());
        return lmsBatchEmailRepository.save(newBatchEmail);
    }

    @DeleteMapping(value = "/{id}")
    public String deleteBatchEmail(@PathVariable Long id) {
        lmsBatchEmailRepository.deleteById(id);
        return "Delete success.";
    }


}
