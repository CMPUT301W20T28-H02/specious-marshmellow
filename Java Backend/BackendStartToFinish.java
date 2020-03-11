public class BackendStartToFinish {
  public static void main (String[] args){

    // Initial Setup

    Wallet riderWallet = new Wallet(20.0);
    Wallet driverWallet = new Wallet(10.0);
    Location riderLocation  = new Location(0.0, 0.0);
    Location driverLocation  = new Location(0.0, 0.0);
    Location startLocation  = new Location(0.0, 0.0);
    Location endLocation  = new Location(1.0, 1.0);

    Rider rider = new Rider ("ma", "am", "m@gmail.com", riderWallet, "(780)123-4567", 5.0, 1, riderLocation);
    Driver driver = new Driver ("hw", "wh", "h@gmail.com", driverWallet, "(780)123-4567", 5.0, 1, driverLocation);


    // Steps we follow:
    // 1. Create a request with a rider, start and end locations
    // 2. Get a driver to accept the open request by calling request.isAcceptedBy(driver)
    //        Note: We can identify open requests in the database using the requestStatus flag (false means request is open)
    // 3. Once both the rider and driver confirm pickup, create a new ride
    //        Note: Rider confirms pickup using riderConfirmation()
    //              Driver confirms pickup using driverConfirmation()
    //              isConfirmedByRiderAndDriver() checks whether both have confirmed pickup
    // 4. When the rider presses the End & Pay button, call ride.endRide()


    Request currentRequest = new Request(rider, startLocation, endLocation);
    // Need to add request to request database
    currentRequest.isAcceptedBy(driver);

    // In our app, these happen in two different onClickListeners, on two different devices
    // Need to keep list of requests and list of rides in a database with up to date info
    currentRequest.riderConfirmation();
    //currentRequest.driverConfirmation();


    if(currentRequest.isConfirmedByRiderAndDriver()){         // If both the rider and driver have confirmed pickup, create a ride

    } else {                                                  // Otherwise, wait for the driver to confirm and create the ride
        while(!currentRequest.isConfirmedByRiderAndDriver()){
            currentRequest.driverConfirmation();  // This happens in the driver's app
        }
    }


    Ride ride = currentRequest.createRide();
    // Need to add ride to ride database

    ride.endRide(ride.getFare());   // Only the driver or the rider can call this: otherwise, we will double charge
                                    // Potential Idea: let the Rider stop the ride at any time, so the rider's app
                                    // will call end ride when the rider presses end and pay

  }
}
