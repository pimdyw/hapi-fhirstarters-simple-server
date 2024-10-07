package ca.uhn.fhir.example;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.HashMap;
import java.util.Map;

public class Example01_PatientResourceProvider implements IResourceProvider {

   private Map<String, Patient> myPatients = new HashMap<String, Patient>();
   private int myNextId = 2;

   /**
    * Constructor
    */
   public Example01_PatientResourceProvider() {
      Patient pat1 = new Patient();
      pat1.setId("1");
      pat1.addIdentifier().setSystem("http://acme.com/MRNs").setValue("7000135");
      pat1.addName().setFamily("Simpson").addGiven("Homer").addGiven("J");
      myPatients.put("1", pat1);
   }

   @Override
   public Class<? extends IBaseResource> getResourceType() {
      return Patient.class;
   }

   /**
    * Simple implementation of the "read" method
    */

   @Read()
   public Patient read(@IdParam IdType theId) {
      Patient retVal = myPatients.get(theId.getIdPart());
      if (retVal == null) {
         throw new ResourceNotFoundException(theId);
      }
      System.out.println("Get Patient Success");
      return retVal;
   }

   @Create
   public MethodOutcome create(@ResourceParam Patient thePatient) {
      // Give the resource the next sequential ID
      int id = myNextId++;
      thePatient.setId(new IdType(id));

      // Store the resource in memory
      myPatients.put(Integer.toString(id), thePatient);

      // Inform the server of the ID for the newly stored resource
      MethodOutcome outcome = new MethodOutcome();
      outcome.setResource(thePatient);
      System.out.println("Create Patient Success");
      return outcome;
   }

   @Update
   public MethodOutcome update(@IdParam IdType theId, @ResourceParam Patient thePatient) {
      // Update the version and last updated time on the resource
      InstantType lastUpdated = InstantType.withCurrentTime();
      thePatient.getMeta().setLastUpdatedElement(lastUpdated);

      // update resource in local database
      myPatients.put(theId.getIdPart(), thePatient);

      // Add the resource to the outcome, so that it can be returned by the server
      // if the client requests it
      MethodOutcome outcome = new MethodOutcome();
      outcome.setResource(thePatient);
      System.out.println("Update Patient Success");
      return outcome;
   }

   @Delete
   public void delete(@IdParam IdType theId) {
      Patient retVal = myPatients.get(theId.getIdPart());
      myPatients.remove(theId.getIdPart(), retVal);
      System.out.println("Delete Patient Success");
   }

}
