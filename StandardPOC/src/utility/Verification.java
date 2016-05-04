package utility;


	
	import static org.junit.Assert.*;

	public class Verification {

		public StringBuilder result = new StringBuilder();

		public void checkForFails(){
			if (result.length() > 0){
				StringBuilder message = new StringBuilder(result);
				result.setLength(0);
				throw new AssertionError(message);
			}
		}

		public boolean verifyTrue(String vp, boolean actual){

			try{
				System.out.println("verifying "+ vp);
				assertTrue(vp,actual);
				return true;
			}catch(AssertionError e){

				System.err.println("FAILED "+ vp);
				result.append("\n"+vp+"\n"+e);
				return false;
			}catch(Exception e){
				System.err.println("FAILED "+ vp);
				result.append("/n"+vp+"\n"+e);
				return false;
			}

		}

		public boolean verifyEquals(String vp, int actual, int expected){
			try{
				System.out.println("verifying "+ vp);
				assertEquals(vp,actual,expected);
				return true;
			}catch(AssertionError e){
				System.err.println("FAILED "+ vp);
				result.append("\n"+vp+"\n"+e);
				return false;
			}catch(Exception e){
				System.err.println("FAILED "+ vp);
				result.append("/n"+vp+"\n"+e);
				return false;
			}
		}

		public boolean verifyEquals(String vp, String actual, String expected){
			try{
				System.out.println("verifying "+ vp);
				assertEquals(vp,actual,expected);
				return true;
			}catch(AssertionError e){
				System.err.println("FAILED "+ vp);
				result.append("\n"+vp+"\n"+e);
				return false;
			}catch(Exception e){
				System.err.println("FAILED "+ vp);
				result.append("/n"+vp+"\n"+e);
				return false;
			}
		}

		public boolean verifyEquals(String vp, Boolean actual, Boolean expected){
			try{
				System.out.println("verifying "+ vp);
				assertEquals(vp,actual,expected);
				return true;
			}catch(AssertionError e){
				System.err.println("FAILED "+ vp);
				result.append("\n"+vp+"\n"+e);
				return false;
			}catch(Exception e){
				System.err.println("FAILED "+ vp);
				result.append("/n"+vp+"\n"+e);
				return false;
			}
		}

		public boolean verifyEquals(String vp, Object actual, Object expected){
			try{
				System.out.println("verifying "+ vp);
				assertEquals(vp,actual,expected);
				return true;
			}catch(AssertionError e){
				System.err.println("FAILED "+ vp);
				result.append("\n"+vp+"\n"+e);
				return false;
			}catch(Exception e){
				System.err.println("FAILED "+ vp);
				result.append("/n"+vp+"\n"+e);
				return false;
			}
		}

		public boolean verifyFalse(String vp, boolean actual){

			try{
				System.out.println("verifying "+ vp);
				assertFalse(vp,actual);
				return true;
			}catch(AssertionError e){
				System.err.println("FAILED "+ vp);
				result.append("\n"+vp+"\n"+e);
				return false;
			}catch(Exception e){
				System.err.println("FAILED "+ vp);
				result.append("/n"+vp+"\n"+e);
				return false;
			}

		}



	}



