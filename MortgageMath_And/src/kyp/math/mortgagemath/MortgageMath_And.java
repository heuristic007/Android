// MortgageMath.java
// Calculates bills using 5, 10, 15 and custom percentage tips.
package kyp.math.mortgagemath;

import java.math.BigDecimal;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

// main Activity class for the TipCalculator
public class MortgageMath_And extends Activity 
{
   // constants used when saving/restoring state
   private static final String  MORTGAGE = "MORTGAGE";
   private static final String  LOANTERM = "LOANTERM";
   private static final String CUSTOM_PERCENT = "CUSTOM_PERCENT";
   
   private Resources res;
   private Context svctxt;
   
   private int locale = 0; //0: English, 1: Traditional Chinese, 2: Simplified Chinese
   private static boolean btnLang_clicked = false; 
   private String currLocale;
   private double currentMortgage; // mortgage amount entered by the user
   private int currentLoanTerm; 
   private int currentGrace = 0;
   private double currentRate;
   private int currentCustomPercent; // tip % set with the SeekBar
   private EditText mortgageEditText; // accepts user input for mortgage
   private EditText loantermEditText; // accepts user input for loan term
   private EditText graceEditText; // accepts user input for grace period
   private EditText rateEditText; // accepts user input for interest rate
   private Button btn_et, btn_ep, btn_lang; //buttons
   private static boolean ll1created = false;
   private LinearLayout ll0, ll1, ll2;
   private final String DARKBLUE = "#00008B";
   private TextView plantitle, mortgagev, loantermv, ratev, gracev, 
   payheadv, payingracev, payaftergracev, paymonthlyprincipalv, totalpayv,
   currentmontotalv;
   private double payingrace, payaftergrace, paymonthlyprincipal;
   private double paymonthlyrate, totalpayment;

   // Called when the activity is first created.
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState); // call superclass's version
      //setContentView(R.layout.main); // inflate the GUI
      
      svctxt = this.getBaseContext(); 
      String LangCode = getResources().getConfiguration().locale.toString();
      //setLanguage(getApplicationContext(), "zh_TW");
      //LangCode = getResources().getConfiguration().locale.toString();
      
      if (!btnLang_clicked) {
    	  //use default locale
    	  String defLang = Locale.getDefault().toString();
    	  setLanguage(getApplicationContext(), defLang);
      }
      setContentView(R.layout.main); // inflate the GUI
      
      currLocale = getResources().getConfiguration().locale.toString();
      if (currLocale.equals("en_us"))
    	locale = 0;
      else if (currLocale.equals("zh_tw"))
        locale = 1;
      
      res = getResources();
      this.setTitle(String.format(res.getString(R.string.app_name)));
    	 
      ll0 = (LinearLayout)findViewById(R.id.LinearLayoutMain);
      
      // check if app just started or is being restored from memory
      if ( savedInstanceState == null ) // the app just started running
      {
         currentMortgage = 0.0; // initialize the mortgage to zero
         currentLoanTerm = 0; // initialize the loan term to zero
         currentCustomPercent = 18; // initialize the custom tip to 18%
      } // end if
      else // app is being restored from memory, not executed from scratch
      {
         // initialize the mortgage to saved amount
          currentMortgage = savedInstanceState.getDouble(MORTGAGE); 
          currentLoanTerm = savedInstanceState.getInt(LOANTERM); 
         
         // initialize the custom tip to saved tip percent 
         currentCustomPercent = 
            savedInstanceState.getInt(CUSTOM_PERCENT); 
      } // end else
      
      // get the mortgageEditText 
      mortgageEditText = (EditText) findViewById(R.id.mortgageEditText);

      // mortgageEditTextWatcher handles billEditText's onTextChanged event
      mortgageEditText.addTextChangedListener(mortgageEditTextWatcher);
      
      // get the loantermEditText 
      loantermEditText = (EditText) findViewById(R.id.loantermEditText);

      // get the graceEditText 
      graceEditText = (EditText) findViewById(R.id.graceEditText);

      // get the loantermEditText 
      rateEditText = (EditText) findViewById(R.id.rateEditText);
      
      btn_et = (Button) findViewById(R.id.btn_et);
      btn_ep = (Button) findViewById(R.id.btn_ep);
      btn_lang = (Button) findViewById(R.id.btn_lang);
      
      btn_et.setOnClickListener(new OnClickListener() {
    	   @Override
    	   public void onClick(View v) {
    		   Resources res = getResources();
    		   /*
    		   String mortgagestr = mortgageEditText.getText().toString();
    		   if (mortgagestr.length()<=0)
    		   {
    			   mortgageEditText.requestFocus();
    			   mortgageEditText.setError( "Mortgage is required!" );
    			   return;
    		   }
    		   */
    		   if (!input_validated())
    		     return;
    		   
    		   //Calculate Equal Total Payment
    	       //Calculate_EqualTotal();
    	       
    	       //Display Equal Total Payment

    	       //Remove Layout ll0 if exists
    	       if (ll1created)
    	       {
    	    	  ll0.removeView(ll1);   
    	       }

    	       // Create Layout ll1
    	       ll1 = new LinearLayout(svctxt);  
    	       ll1.setOrientation(LinearLayout.VERTICAL);  
    	       ll0.addView(ll1);
    	       ll1created = true;

    	       // Create TextView
               plantitle = new TextView(svctxt);
               //plantitle.setText(" Calculate Equal Total");
               plantitle.setText(String.format(res.getString(R.string.CalcEqTotal)));
               //mortgagev.setTextColor(Color.parseColor("#00008B"));
               plantitle.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(plantitle);
    	       // Create TextView
               mortgagev = new TextView(svctxt);
               //mortgagev.setText(" Mortgage: "+ currentMortgage + " dollars");
               mortgagev.setText(" " + String.format(res.getString(R.string.mortgage)) +
            	 ": " + currentMortgage + 
            	 String.format(res.getString(R.string.mortgageUnit)));	
               //mortgagev.setTextColor(Color.parseColor("#00008B"));
               mortgagev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(mortgagev);
    	       // Create TextView
               loantermv = new TextView(svctxt);
               //loantermv.setText(" Loan Term: "+ currentLoanTerm+" Years");
               loantermv.setText(" " + String.format(res.getString(R.string.loanterm)) +
                  	 ": " + currentLoanTerm + 
                	 String.format(res.getString(R.string.loantermUnit)));	
               loantermv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(loantermv);
    	       // Create TextView
               ratev = new TextView(svctxt);
               //ratev.setText(" Interest Rate: "+ currentRate+ "%");
               ratev.setText(" " + String.format(res.getString(R.string.rate)) +
                    	 ": " + currentRate + "%");	
               ratev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(ratev);
    	       // Create TextView
               gracev = new TextView(svctxt);
               //gracev.setText(" Grace Period: "+ currentGrace+" Years");
               gracev.setText(" " + String.format(res.getString(R.string.grace)) +
                  	 ": " + currentGrace + 
                  	 String.format(res.getString(R.string.graceUnit)));	
               gracev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(gracev);
    	       // Create TextView
               payheadv = new TextView(svctxt);
               //payheadv.setText(" Payment every month: ");
               payheadv.setText(" " + String.format(res.getString(R.string.PayPerMonth)));
               payheadv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(payheadv);

               if (currentGrace > 0)
                 payingrace = Math.round((currentMortgage*(currentRate/100)/12)*100.00)/100.00;
               else
            	 payingrace = 0.0;
               
    	       // Create TextView
               payingracev = new TextView(svctxt);
               //payingracev.setText(" Payment in grace period : "+ payingrace + " dollars");
               payingracev.setText(" " + String.format(res.getString(R.string.PayInGrace)) +
                    	 payingrace + 
                      	 String.format(res.getString(R.string.mortgageUnit)));		
               payingracev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(payingracev);
               
               int paymonths = (currentLoanTerm - currentGrace)*12;
               double monrate = (currentRate/100)/12;
               double exprate = Math.pow(1+monrate,paymonths);
               //payaftergrace = ((((1+(currentRate/12))**paymonths)*(currentRate/12))/(((1+(currentRate/12))**paymonths)-1))*currentMortgage;
               payaftergrace = Math.round((((exprate*monrate)/(exprate-1))*currentMortgage)*100.00)/100.00;
    	       // Create TextView
               payaftergracev = new TextView(svctxt);
               //payaftergracev.setText(" Monthly payment after grace: " + payaftergrace + " dollars");
               payaftergracev.setText(" " + String.format(res.getString(R.string.PayAfterGrace)) +
                  	 payaftergrace + 
                  	 String.format(res.getString(R.string.mortgageUnit)));		
               payaftergracev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(payaftergracev);
               
               totalpayment = payingrace + payaftergrace*paymonths;
               totalpayment = new BigDecimal(totalpayment).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
               totalpayv = new TextView(svctxt);
               //totalpayv.setText(" Total Payment: " + totalpayment + " dollars");
               totalpayv.setText(" " + String.format(res.getString(R.string.TotalPayment)) +
                    	 totalpayment + 
                    	 String.format(res.getString(R.string.mortgageUnit)));		

               totalpayv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(totalpayv);               
               
    	   }
      });
      
      btn_ep.setOnClickListener(new OnClickListener() {
   	       public void onClick(View v) {
   	    	   Resources res = getResources();
   	    	   
    		   if (!input_validated())
      		     return;
      		   
   	           //Calculate_EqualPrincipal();
    		   
    	       //Display Equal Principal Payment

    	       //Remove Layout ll0 if exists
    	       if (ll1created)
    	       {
    	    	  ll0.removeView(ll1);   
    	       }

    	       // Create Layout ll1
    	       ll1 = new LinearLayout(svctxt);  
    	       ll1.setOrientation(LinearLayout.VERTICAL);  
    	       ll0.addView(ll1);
    	       ll1created = true;

    	       // Create TextView
               plantitle = new TextView(svctxt);
               //plantitle.setText(" Calculate Principal Total");
               plantitle.setText(String.format(res.getString(R.string.CalcPrTotal)));
               //mortgagev.setTextColor(Color.parseColor("#00008B"));
               plantitle.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(plantitle);
    	       // Create TextView
               mortgagev = new TextView(svctxt);
               //mortgagev.setText(" Mortgage: "+ currentMortgage + " dollars");
               mortgagev.setText(" " + String.format(res.getString(R.string.mortgage)) +
            	 ": " + currentMortgage + 
            	 String.format(res.getString(R.string.mortgageUnit)));	
               //mortgagev.setTextColor(Color.parseColor("#00008B"));
               mortgagev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(mortgagev);
    	       // Create TextView
               loantermv = new TextView(svctxt);
               //loantermv.setText(" Loan Term: "+ currentLoanTerm+" Years");
               loantermv.setText(" " + String.format(res.getString(R.string.loanterm)) +
                    	 ": " + currentLoanTerm + 
                  	 String.format(res.getString(R.string.loantermUnit)));	
               loantermv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(loantermv);
    	       // Create TextView
               ratev = new TextView(svctxt);
               //ratev.setText(" Interest Rate: "+ currentRate+ "%");
               ratev.setText(" " + String.format(res.getString(R.string.rate)) +
                  	 ": " + currentRate + "%");	
               ratev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(ratev);
    	       // Create TextView
               gracev = new TextView(svctxt);
               //gracev.setText(" Grace Period: "+ currentGrace+" Years");
               gracev.setText(" " + String.format(res.getString(R.string.grace)) +
                    	 ": " + currentGrace + 
                    	 String.format(res.getString(R.string.graceUnit)));	
               gracev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(gracev);
    	       // Create TextView
               payheadv = new TextView(svctxt);
               //payheadv.setText(" Payment every month: ");
               payheadv.setText(" " + String.format(res.getString(R.string.PayPerMonth)));
               payheadv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(payheadv);

               if (currentGrace > 0)
                 payingrace = Math.round((currentMortgage*(currentRate/100)/12)*100.00)/100.00;
               else
            	 payingrace = 0.0;
               
    	       // Create TextView
               payingracev = new TextView(svctxt);
               //payingracev.setText(" Payment in grace period : "+ payingrace + " dollars");
               payingracev.setText(" " + String.format(res.getString(R.string.PayInGrace)) +
                  	 payingrace + 
                    	 String.format(res.getString(R.string.mortgageUnit)));		
               payingracev.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(payingracev);    
               
               int paymonths = (currentLoanTerm - currentGrace)*12;
               double monrate = (currentRate/100)/12;
               paymonthlyprincipal = Math.round((currentMortgage/paymonths)*100.00)/100.00;
               
    	       // Create TextView
               paymonthlyprincipalv = new TextView(svctxt);
               //paymonthlyprincipalv.setText(" Fixed Principal Monthly Payment : "
               //+ paymonthlyprincipal + " dollars");
               paymonthlyprincipalv.setText(
            		   " " + String.format(res.getString(R.string.FixedPrPay)) +
                    	 paymonthlyprincipal + 
                      	 String.format(res.getString(R.string.mortgageUnit)));		
               paymonthlyprincipalv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(paymonthlyprincipalv);    

    	       // Create Layout ll2
    	       ll2 = new LinearLayout(svctxt);  
    	       ll2.setOrientation(LinearLayout.VERTICAL);  

               double totalrate = 0.0;
               for (int i=0; i <paymonths; i++)
               {
            	  double currmonrate0 = (currentMortgage - i*paymonthlyprincipal)*monrate;
            	  double currmonrate = Math.round(currmonrate0*100.00)/100.00;
                  double currmontotal = currmonrate + paymonthlyprincipal;
                  int k = i+1;
       	          // Create TextView
                  currentmontotalv = new TextView(svctxt);
                  //currentmontotalv.setText(" Month "+ k + ": "+ currmontotal+ " dollars");
                  currentmontotalv.setText(" " + String.format(res.getString(R.string.Month)) +
                		  k + ": " + currmontotal + 
            	          String.format(res.getString(R.string.mortgageUnit)));		
                  //currentmontotalv.setText(" Month "+ k + ": "+ currmonrate+ " dollars");
                  currentmontotalv.setTextColor(Color.parseColor(DARKBLUE));
                  ll2.addView(currentmontotalv);    
                  
            	  totalrate = totalrate + currmonrate;
               }
               
               totalpayment = payingrace + currentMortgage + totalrate;
               totalpayment = new BigDecimal(totalpayment).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
               totalpayv = new TextView(svctxt);
               //totalpayv.setText(" Total Payment: " + totalpayment + " dollars");
               totalpayv.setText(" " + String.format(res.getString(R.string.TotalPayment)) +
                  	 totalpayment + 
                  	 String.format(res.getString(R.string.mortgageUnit)));		
               totalpayv.setTextColor(Color.parseColor(DARKBLUE));
               ll1.addView(totalpayv);               

               ll1.addView(ll2);
               
   	       }
      });
      
      btn_lang.setOnClickListener(new OnClickListener() {
   	   @Override
   	   public void onClick(View v) {

   		   btnLang_clicked = true; 
   		   Context ctxt = getApplicationContext();
   	       Button b = (Button)v;
           String btnText = b.getText().toString();
           if (btnText.equals("Chinese")) {
   	         setLanguage(ctxt, "zh_TW");
   	       //String LangCode = getResources().getConfiguration().locale.toString();
           }
           else
           {
             setLanguage(ctxt, "en_US");	   
           }
   		   //Code for Intent recreation
   		   Intent intent = getIntent();
   		   finish();
   		   startActivity(intent);
   		
   	   }
     });
            
   } // end method onCreate

   // button handling

   private boolean input_validated()
   {
	   String mortgagestr = mortgageEditText.getText().toString();
	   if (mortgagestr.length()<=0)
	   {
		   mortgageEditText.requestFocus();
		   mortgageEditText.setError( "Mortgage is required!" );
		   return false;
	   }
	   else 
		   currentMortgage = Double.parseDouble(mortgagestr);

	   String loantermstr = loantermEditText.getText().toString();
	   if (loantermstr.length()<=0)
	   {
		   loantermEditText.requestFocus();
		   loantermEditText.setError( "Loan Term is required!" );
		   return false;
	   }
	   else 
		   currentLoanTerm = Integer.parseInt(loantermstr);
	   
	   String ratestr = rateEditText.getText().toString();
	   if (ratestr.length()<=0)
	   {
		   rateEditText.requestFocus();
		   rateEditText.setError( "Interest Rate is required!" );
		   return false;
	   }
	   else 
		   currentRate = Double.parseDouble(ratestr);


	   String gracestr = graceEditText.getText().toString();
	   if (gracestr.length()<=0)
		  currentGrace = 0;
	   else
		  currentGrace = Integer.parseInt(gracestr); 

	   return true;
	   
   }
   
   private void Calculate_EqualTotal()
   {
	   currentMortgage = Double.parseDouble(mortgageEditText.getText().toString());
	   AlertDialog ad = new AlertDialog.Builder(this).create();  
	   ad.setCancelable(false); // This blocks the 'BACK' button  
	   ad.setMessage(Double.toString(currentMortgage));  
	   ad.setButton("OK", new DialogInterface.OnClickListener() {  
	       @Override  
	       public void onClick(DialogInterface dialog, int which) {  
	           dialog.dismiss();                      
	       }  
	   });  
	   ad.show();  
	   
   }
   
   private void Calculate_EqualPrincipal()
   {
	   
   }   
   
   // save values of billEditText and customSeekBar
   @Override
   protected void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      
      outState.putDouble(MORTGAGE, currentMortgage);
      outState.putInt(LOANTERM, currentLoanTerm);
      outState.putInt(CUSTOM_PERCENT, currentCustomPercent);
   } // end method onSaveInstanceState
   
   // event-handling object that responds to mortgageEditText's events
   private TextWatcher mortgageEditTextWatcher = new TextWatcher() 
   {
      // called when the user enters a number
      @Override
      public void onTextChanged(CharSequence s, int start, 
         int before, int count) 
      {         
         // convert mortgageEditText's text to a double
         try
         {
            currentMortgage = Double.parseDouble(s.toString());
     	   if (s.length()<=0)
     		     mortgageEditText.setError( "Mortgage is required!" );

         } // end try
         catch (NumberFormatException e)
         {
            currentMortgage = 0.0; // default if an exception occurs
         } // end catch 

      } // end method onTextChanged

      @Override
      public void afterTextChanged(Editable s) 
      {
    	   if (mortgageEditText.getText().toString().length()<=0)
   		     mortgageEditText.setError( "Mortgage is required!" );

      } // end method afterTextChanged

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
         int after) 
      {
      } // end method beforeTextChanged
      
   }; // end billEditTextWatcher
   
   public static void setLanguage(Context context, String LangCode){
 	  Resources res = context.getResources();
 	  DisplayMetrics dm = res.getDisplayMetrics();
 	  android.content.res.Configuration conf = res.getConfiguration();
 	  conf.locale = new Locale(LangCode.toLowerCase());
 	  res.updateConfiguration(conf, dm);
   }
   
} // end class MortgageMath_And
   