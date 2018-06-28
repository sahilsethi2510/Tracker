#include <LPC21xx.H>
#include "uart1.h"
#include "uart0.h"
#include "GPS_LAT_LONG.h"

int main()
	{
	int i=0;
	char r[500];
	uart0_init(9600);
	uart1_init(9600);
	//wifi commands		
	uart0_str("AT\r\n");
	delay_ms(10);
	uart0_str("AT+CWMODE=1\r\n");	  //station
	delay_ms(10);
	uart0_str("AT+CIPMUX=1\r\n");	  //single server
	delay_ms(10);
	while(1)
		{
		r[i++]=uart1_rec();
		// waiting for $GPRMC 
		if((r[i-1]==',')&&(r[i-2]=='C')&&(r[i-3]=='M')&&(r[i-4]=='R')&&(r[i-5]=='P')&&(r[i-6]=='G')&&(r[i-7]=='$'))
			{
			//	$GPRMC COMMAND FOUND
			i=0;	
			gps2();
			delay_ms(5000);
			}
		}				 
	}
