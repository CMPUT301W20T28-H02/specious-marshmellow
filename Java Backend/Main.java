public class Main {
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

    System.out.println(riderWallet.getBalance());
    System.out.println(driverWallet.getBalance());


    // Steps we follow:
    // 1. Create a request with a rider, start and end locations
    // 2. Get a driver to accept the open request by calling request.isAcceptedBy(driver)
    //        Note: We can identify open requests in the database using the requestStatus flag (false means request is open)
    // 3. Once both the rider and driver confirm pickup, create a new ride
    //        Note: Rider confirms pickup using riderConfirmation()
    //              Driver confirms pickup using driverConfirmation()
    //              isConfirmedByRiderAndDriver() checks whether both have confirmed pickup
    // 4. When the rider presses the End & Pay button, call ride.endRide()

    // Best to have one activity with one layout (identical for riders and drivers),
    // we can check whether the user is a rider or driver when the Confirm Pickup button is clicked
    // and then call the appropriate method

    Request currentRequest = new Request(rider, startLocation, endLocation);

    currentRequest.isAcceptedBy(driver);

    System.out.println("Rider Confirmation Initial: " + currentRequest.getRiderConfirmation());
    System.out.println("Driver Confirmation Initial: " + currentRequest.getDriverConfirmation());

    // In our app, these happen in two different onClickListeners, on two different devices
    // Need to keep list of requests and list of rides in a database with up to date info
    currentRequest.riderConfirmation();
    //currentRequest.driverConfirmation();

    System.out.println("Rider Confirmation Final: " + currentRequest.getRiderConfirmation());
    System.out.println("Driver Confirmation Final: " + currentRequest.getDriverConfirmation());

    // This code should all be in rider, although both rider and driver need access to the
    // same request so that they both can confirm pickup
    if(currentRequest.isConfirmedByRiderAndDriver()){         // If both the rider and driver have confirmed pickup, create a ride

    } else {
        while(!currentRequest.isConfirmedByRiderAndDriver()){ // Otherwise, wait for the driver to confirm and create the ride
            try{                    // Put to sleep for 1 second to see this working, this is the time we are waiting for the
              Thread.sleep(1000);   // driver to hit the Confirm Pickup button
            } catch (Exception e){}
            currentRequest.driverConfirmation();  // This happens in the driver's app
            System.out.println("Driver Confirmation Final: " + currentRequest.getDriverConfirmation());
        }
    }

    Ride ride = currentRequest.createRide();
    // Need to add ride to ride database

    ride.endRide(ride.getFare());   // Only the driver or the rider can call this: otherwise, we will double charge
                                    // Potential Idea: let the Rider stop the ride at any time, so the rider's app
                                    // will call end ride when the rider presses end and pay

    System.out.println(riderWallet.getBalance());
    System.out.println(driverWallet.getBalance());


  }
}
