
public class InvalidAge extends RuntimeException{
	InvalidAge(){
		super("\nAge Over 24 Required.");
	}
	InvalidAge(String msg){
		super(msg);
	}
}
