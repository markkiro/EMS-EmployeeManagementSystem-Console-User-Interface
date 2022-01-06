
public class RequiredField extends RuntimeException{
	RequiredField(){
		super("\nFields Cannot be Empty.");
	}
	RequiredField(String msg){
		super(msg);
	}
}
