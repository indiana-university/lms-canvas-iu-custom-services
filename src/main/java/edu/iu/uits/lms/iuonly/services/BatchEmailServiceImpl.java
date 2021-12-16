package edu.iu.uits.lms.iuonly.services;

import edu.iu.uits.lms.iuonly.model.LmsBatchEmail;
import edu.iu.uits.lms.iuonly.repository.LmsBatchEmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yingwang on 10/29/15.
 */
@Service
@Slf4j
public class BatchEmailServiceImpl {

    @Autowired
    private LmsBatchEmailRepository lmsBatchEmailRepository;

    public LmsBatchEmail getBatchEmailFromId(Long id) {
        return lmsBatchEmailRepository.findById(id).orElse(null);
    }

    public LmsBatchEmail getBatchEmailFromGroupCode(String groupcode) {
        return lmsBatchEmailRepository.getBatchEmailFromGroupCode(groupcode);
    }

}
