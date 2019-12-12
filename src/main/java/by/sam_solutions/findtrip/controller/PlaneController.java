package by.sam_solutions.findtrip.controller;

import by.sam_solutions.findtrip.controller.dto.ApiError;
import by.sam_solutions.findtrip.controller.dto.PlaneDTO;
import by.sam_solutions.findtrip.service.CompanyService;
import by.sam_solutions.findtrip.service.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/planes")
public class PlaneController {

    @Autowired
    PlaneService planeService;

    @Autowired
    CompanyService companyService;


    @GetMapping(path = {"/edit", "/edit/{id}"})
    public String getAddOrEditCityView(
            Model model,
            @RequestParam(name = "company", required = false) String company,
            @PathVariable(value = "id") Optional<Long> id) throws EntityNotFoundException {

        if (id.isPresent()) {
            PlaneDTO planeDTO = planeService.findOne(id.get());
            if (planeDTO != null) {
                model.addAttribute("companyName", planeDTO.getCompanyDTO().getName());
                model.addAttribute("companyId", planeDTO.getCompanyDTO().getId());
                model.addAttribute("plane", planeDTO);
            } else {
                throw new EntityNotFoundException("Plane with id=" + id + " not found");
            }

            return "plane/editPlane";
        } else {
            model.addAttribute("companyName", company);
            model.addAttribute("plane", new PlaneDTO());
            return "plane/editPlane";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCountry(@PathVariable(value = "id") Long id) {
        Long idCompany = planeService.getCompanyIdByPlaneId(id);
        planeService.deleteById(id);
        return idCompany==null ? "redirect:/companies" : "redirect:/companies/"+idCompany+"/planes";
    }


    @PostMapping(path = "/edit")
    public String addOrEditCountry(@Valid @ModelAttribute("plane") PlaneDTO planeDTO,
                                   @RequestParam("companyName") String companyName,
                                   BindingResult result, Model model) {
        Long companyId = companyService.getCompanyIdByName(companyName);
        if (result.hasErrors()) {
            ApiError apiError = new ApiError();
            String message = "";
            for (FieldError str : result.getFieldErrors()) {
                message += str.getDefaultMessage();
                apiError.setMessage(message);
            }
            model.addAttribute("plane", planeDTO);
            model.addAttribute("companyId", companyId);
            model.addAttribute("companyName", companyName);
            model.addAttribute("apiError", apiError);
            return "plane/editPlane";
        }

        planeService.saveOrUpdate(planeDTO, companyId, companyName);


        return "redirect:/companies/"+companyId+"/planes";
    }


}