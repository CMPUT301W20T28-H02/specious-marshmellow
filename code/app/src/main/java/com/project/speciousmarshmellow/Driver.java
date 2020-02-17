public Driver extends User {
  private double rating;

  public Driver(String username, String password, String email, Wallet wallet, String phone, double rating, Location location, double rating) {
    super(String username, String password, String email, Wallet wallet, String phone, Double rating, Location location);
    this.rating = rating;
  }

  public void acceptRequest(Request request) {

  }

  public void getPaid(double amount) {

  }

  public void searchRequests() {

  }

  public double getRating(){
    return rating;
  }

  public double setRating(double rating){
    this.rating = rating;
  }
}
