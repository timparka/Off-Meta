package com.offmeta.gg.Controller;

import com.offmeta.gg.DTO.OffMetaDTO;
import com.offmeta.gg.Service.OffMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participant")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OffMetaController {
    @Autowired
    private OffMetaService offMetaService;

    @GetMapping("/{lane}")
    @ResponseStatus(HttpStatus.OK)
    public OffMetaDTO offMetaPick(@PathVariable String lane) {
        return offMetaService.getOffMetaPick(lane);
    }
}