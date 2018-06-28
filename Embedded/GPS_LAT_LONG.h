// GPS LAT AND LONG DETECTION


int R[100],i=0,flag=0,flag1=1,flag2=0,x,c=0;

// delay function

void delay_ms(unsigned int ms)
	{
		T0MCR=0x04;  //set MR0 for counting
		T0MR0=(3000*5*ms);//1 msec delay counting value foe 15MHz
		T0TC=0x00;//make sure  TC is 0
		T0TCR=0x01;// start timer 0
		while(T0TC<T0MR0);// wait till T0TC matches MR0
	}

void gps2()																				   
	{	
	char ch1[15];
	char ch2[15];
	char r[100];
	int i=0;
	int j=0;
	int h=0;
	int b=0;

	// RECIVEING GPS DATA
	do
		{
		r[i++]=uart1_rec();
		}
	//WAITING FOR A COMMA
	while(r[i-1]!=42);

	//SETTING VALUES OF LONGITUDE
	j=13;	
	if(j==13)
		{	
		do
			{	
			ch1[h++]=r[j++];
			}
		while((r[j-12]!=',')&&(j<22));	   
		}
	ch1[h]='\0';
 	
	//SETTING VALUES OF LATTITUDE
	j=25;		   
	if(j==25)
		{	
		do
			{	
			ch2[b++]=r[j++];
			}
		while((r[j-13]!=',')&&(j<35));
		}
	ch2[b]='\0';
	i=0;
	uart0_str("AT+CIPSTART=4,\"TCP\",\"api.thingspeak.com\",80\r\n");
	delay_ms(4000);
	uart0_str("AT+CIPSEND=4,69\r\n");
	delay_ms(20);
	uart0_str("GET /update?key=T04BJE326FGVWY9F&field1=");
	uart0_str(ch1);
	uart0_str("&field2=");
	uart0_str(ch2);
	uart0_str("\r\n");
	uart0_str(" HTTP/1.1");
	// cntl+m
	uart0_trans(13);
	// cntl+j
	uart0_trans(10);
	uart0_str("host:api.thingspeak.com");
	// cntl+m
	uart0_trans(13);
	// cntl+j
	uart0_trans(10);
	uart0_str("connection: keep-alive");
	// cntl+m
	uart0_trans(13);
	// cntl+j
	uart0_trans(10);
	// cntl+m
	uart0_trans(13);
	// cntl+j
	uart0_trans(10);
	// cntl+z
	uart0_trans(26);
	}
