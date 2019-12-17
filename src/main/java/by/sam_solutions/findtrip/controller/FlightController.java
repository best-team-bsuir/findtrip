package by.sam_solutions.findtrip.controller;

import by.sam_solutions.findtrip.controller.dto.*;
import by.sam_solutions.findtrip.service.CityService;
import by.sam_solutions.findtrip.service.CompanyService;
import by.sam_solutions.findtrip.service.CountryService;
import by.sam_solutions.findtrip.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    FlightService flightService;

    @Autowired
    CountryService countryService;

    @Autowired
    CityService cityService;

    @Autowired
    CompanyService companyService;

    List<CountryDTO> countryDTOList;
    List<CompanyDTO> companyDTOList;

    @ModelAttribute("countries")
    public List<CountryDTO> getCountries() {
        countryDTOList = countryService.findAll();
        return  countryDTOList;
    }

    @GetMapping("/countries")
    @ResponseBody
    public List<CountryDTO> getCountriesJson(){
        return countryDTOList;
    }

    @ModelAttribute("companies")
    public List<CompanyDTO> getCompanies() {
        companyDTOList = companyService.findAll();
        return companyDTOList;
    }

    @GetMapping("/companies")
    @ResponseBody
    public List<CompanyDTO> getCompaniesJson(){
        return companyDTOList;
    }


    @GetMapping("{id}")
    public String getEditFlightView(@PathVariable(value = "id") Long id) {
        return "flight/addFlight";
    }

    @GetMapping("/create")
    public String getAddFlightView(Model model) {
        model.addAttribute("flight", new FlightDTO());
        return "flight/addFlight";
    }

    @GetMapping("/edit/{id}")
    public String getEditFlightView(@PathVariable Long id,Model model) {
        FlightDTO flightDTO = flightService.getById(id);

        String dDeparture =new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(flightDTO.getDepartureDate().getTime()));
        String dArrival =new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(flightDTO.getArrivalDate().getTime()));

        model.addAttribute("id", flightDTO.getId());
        model.addAttribute("countries_fr", flightDTO.getAirportDeparture().getCityDTO().getCountryDTO());
        model.addAttribute("countries_to",flightDTO.getAirportArrival().getCityDTO().getCountryDTO());
        model.addAttribute("cities_fr",flightDTO.getAirportDeparture().getCityDTO());
        model.addAttribute("cities_to",flightDTO.getAirportArrival().getCityDTO());
        model.addAttribute("airports_fr",flightDTO.getAirportDeparture());
        model.addAttribute("airports_to",flightDTO.getAirportArrival());
        model.addAttribute("picker1", dDeparture);
        model.addAttribute("picker2",dArrival);
        model.addAttribute("companies", flightDTO.getPlane().getCompanyDTO());
        model.addAttribute("planes", flightDTO.getPlane());
        model.addAttribute("allSeats",flightDTO.getAllSeats());
        model.addAttribute("freeSeats", flightDTO.getFreeSeats());
        model.addAttribute("price", flightDTO.getPrice());
        return "flight/editFlight";
    }

    @GetMapping(value = "/countries/{id}")
    @ResponseBody
    public List<CityDTO> getCities(@PathVariable Long id) {
        List<CityDTO> list = cityService.getCityListByCountry(id);
        return list;
    }

    @GetMapping(value = "/cities/{id}")
    @ResponseBody
    public List<AirportDTO> getAirports(@PathVariable Long id) {
        CityDTO cityDTO = cityService.findOne(id);
        return cityDTO.getAirportDTOList();
    }

    @GetMapping(value = "/companies/{id}")
    @ResponseBody
    public List<PlaneDTO> getPlanes(@PathVariable Long id) {
        CompanyDTO companyDTO = companyService.findOne(id);
        return companyDTO.getPlaneDTOList();
    }

    @PostMapping()
    public String addFlight(@RequestBody FlightCreateUpdateDTO flightDTO) {
        flightService.addFlight(flightDTO);
        return "flight/addFlight"; // edit on show flights
    }

    @PostMapping("/{id}")
    public String addFlight(@PathVariable Long id,
                            @RequestBody FlightCreateUpdateDTO flightDTO) {
        flightService.edit(flightDTO);
        return "flight/editFlight";
    }


}
