package by.sum_solutions.findtrip.controller;

import by.sum_solutions.findtrip.controller.dto.ApiError;
import by.sum_solutions.findtrip.controller.dto.CityDTO;
import by.sum_solutions.findtrip.controller.dto.CountryDTO;
import by.sum_solutions.findtrip.exception.EditCityParametersExistException;
import by.sum_solutions.findtrip.exception.EditCountryParametersExistException;
import by.sum_solutions.findtrip.service.CityService;
import by.sum_solutions.findtrip.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/cities")
public class CityController {

    @Autowired
    CityService cityService;

    @Autowired
    CountryService countryService;

    @GetMapping(path = {"/edit", "/edit/{id}"})
    public String getAddOrEditCityView(
            Model model,
            @RequestParam(name = "country", required = false) String country,
            @PathVariable(value = "id") Optional<Long> id ) throws EntityNotFoundException {

        if (id.isPresent()) {
            CityDTO cityDTO = cityService.findOne(id.get());
            if (cityDTO != null) {
                model.addAttribute("countryName", cityDTO.getCountryDTO().getName());
                model.addAttribute("city", cityDTO);
            } else {
                throw new EntityNotFoundException("City with id=" + id + " not found");
            }

            return "city/editCity";
        } else {
            CityDTO cityDTO = new CityDTO();
            model.addAttribute("countryName", country);
            model.addAttribute("city", cityDTO);
            return "city/editCity";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCountry(@PathVariable(value = "id") Long id){
        cityService.delete(id);
        return "redirect:/country";
    }


    @PostMapping(path = "/edit")
    public String addOrEditCountry(@Valid @ModelAttribute("city") CityDTO cityDTO,
                                   @RequestParam(name = "countryName") String countryName,
                                   BindingResult result,
                                   Model model) {

        if (result.hasErrors()) {
            ApiError apiError = new ApiError();
            String message = "";
            for (FieldError str : result.getFieldErrors()) {
                message += str.getDefaultMessage();
                apiError.setMessage(message);
            }
            model.addAttribute("city",cityDTO);
            model.addAttribute("countryName",cityDTO);
            model.addAttribute("apiError",apiError);
            return "city/editCity";
        }

        if (cityDTO.getId() != null) {

            if (cityService.getCityIdByName(cityDTO.getName()) != null
                    && cityService.getCityIdByName(cityDTO.getName()) != cityDTO.getId()) {
                throw new EditCityParametersExistException("City_with_this_name_already_exist", cityDTO, countryName);
            }
            cityService.saveOrUpdate(cityDTO, null);
        } else {
            if (cityService.getCityIdByName(cityDTO.getName()) != null) {
                throw new EditCityParametersExistException("City_with_this_name_already_exist", cityDTO, countryName);
            }
            cityService.saveOrUpdate(cityDTO, countryName);
        }
        return "redirect:/country";
    }


}