Public class User {
	private String username;
	private String password;
	private String email;
	private Wallet wallet;
	private String phone;
	private double rating;
	private Location location;

	User(String username, String password, String email, 
		Wallet wallet, String phone, Double rating, Location location){
		this.username = username;
		this.password = password;
		this.email = email;
		this.wallet = wallet;
		this.phone = phone;
		this.rating = rating;
		this.location = location;

	}

	public void viewProfile(){}

	public void editProfile(){}

	public void openWallet(Wallet wallet){}

	public abstract viewRequest(){}

	public void confirmPickup(Location location){}

}