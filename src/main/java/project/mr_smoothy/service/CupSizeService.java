package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.CupSizeCreateRequest;
import project.mr_smoothy.dto.request.CupSizeUpdateRequest;
import project.mr_smoothy.dto.response.CupSizeResponse;
import project.mr_smoothy.entity.CupSize;
import project.mr_smoothy.repository.CupSizeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CupSizeService {

    private final CupSizeRepository cupSizeRepository;

    public CupSizeResponse create(CupSizeCreateRequest request) {
        if (cupSizeRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Cup size name already exists");
        }
        CupSize cs = new CupSize();
        cs.setName(request.getName());
        cs.setSizeInMl(request.getVolumeMl());
        cs.setPrice(request.getPriceExtra());
        cs.setActive(request.getActive() != null ? request.getActive() : true);
        return toResponse(cupSizeRepository.save(cs));
    }

    public CupSizeResponse update(Long id, CupSizeUpdateRequest request) {
        CupSize cs = cupSizeRepository.findById(id).orElseThrow(() -> new RuntimeException("Cup size not found"));
        if (request.getName() != null) cs.setName(request.getName());
        if (request.getVolumeMl() != null) cs.setSizeInMl(request.getVolumeMl());
        if (request.getPriceExtra() != null) cs.setPrice(request.getPriceExtra());
        if (request.getActive() != null) cs.setActive(request.getActive());
        return toResponse(cupSizeRepository.save(cs));
    }

    public void delete(Long id) {
        if (!cupSizeRepository.existsById(id)) throw new RuntimeException("Cup size not found");
        cupSizeRepository.deleteById(id);
    }

    public CupSizeResponse get(Long id) {
        return cupSizeRepository.findById(id).map(this::toResponse).orElseThrow(() -> new RuntimeException("Cup size not found"));
    }

    @Transactional(readOnly = true)
    public List<CupSizeResponse> list() {
        return cupSizeRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CupSizeResponse> listActive() {
        return cupSizeRepository.findByActiveTrue().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CupSizeResponse toResponse(CupSize c) {
        return CupSizeResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .volumeMl(c.getSizeInMl())
                .priceExtra(c.getPrice())
                .active(c.getActive())
                .build();
    }
}


