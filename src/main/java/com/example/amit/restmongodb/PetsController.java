package com.example.amit.restmongodb;

import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.amit.restmongodb.exception.ResourceNotFoundException;
import com.example.amit.restmongodb.models.Pets;
import com.example.amit.restmongodb.repositories.PetsRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;



@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = "http://localhost:8080")
@Api(value="Pets Management System", description="Operations pertaining to pets in Pets Management System")
public class PetsController {
	@Autowired
	private PetsRepository repository;

	 private static final Logger logger = LoggerFactory.getLogger(PetsController.class);
	 
	@ApiOperation(value = "View a list of available pets", response = List.class)
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Successfully retrieved list"),
		    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		    @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
		})
	@GetMapping(value = "/")
	public List<Pets> getAllPets() {
		return repository.findAll();
	}

	@ApiOperation(value = "View a selected pet based on id", response = Pets.class)
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Successfully retrieved selected pet"),
		    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		    @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
		})
	@GetMapping(value = "/{id}")
	public Pets getPetById(@ApiParam(value = "Pet id from which pet object will retrieve", required = true)
		@PathVariable("id") ObjectId id) throws ResourceNotFoundException {
		logger.info("ID for retrieval is " + id);
		Pets pet = repository.findBy_id(id);
		if(pet == null)
				//.orElseThrow(() -> new ResourceNotFoundException("Pet not found for this id :: " + id));		
			throw new ResourceNotFoundException("Pet not found for this id :: " + id);		
		return pet;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public void modifyPetById(@PathVariable("id") ObjectId id, @Valid @RequestBody Pets pets) {
		pets.set_id(id);
		repository.save(pets);
	}
	
	@ApiOperation(value = "Add a pet")
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Added successfully"),
		    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
		    @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		    @ApiResponse(code = 422, message = "Unprocessable entity")
		})
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Pets createPet(@Valid @RequestBody Pets pets) {
	  pets.set_id(ObjectId.get());
	  repository.save(pets);
	  return pets;
	}
	
	@ApiOperation(value = "Delete a pet")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deletePet(@PathVariable ObjectId id) {
	  repository.delete(repository.findBy_id(id));
	}
	
}
