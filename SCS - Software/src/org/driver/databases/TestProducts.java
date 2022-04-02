////Brody Long - 30022870 
//
//package org.driver.databases;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//
//import org.controlSoftware.data.ItemProduct;
//import org.lsmr.selfcheckout.Barcode;
//import org.lsmr.selfcheckout.Numeral;
//import org.lsmr.selfcheckout.products.BarcodedProduct;
//
//public class TestProducts {
//	
//	public ArrayList<Barcode> BarcodeList = new ArrayList<Barcode>();
//	
//	public ArrayList<BarcodedProduct> BPList = new ArrayList<BarcodedProduct>();
//	
//	public TestProducts()
//	{
//		// Product 1
//		Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
//		BarcodedProduct P1 = new BarcodedProduct(barcode1,
//				4000,
//				"4L Milk Jug.",
//				new BigDecimal("4.89"));
//		BarcodeList.add(barcode1);
//		BPList.add(P1);
//		// Product 1
//		
//		// ItemProduct 2
//		Barcode barcode2 = new Barcode(new Numeral[] {Numeral.two});
//		BarcodedProduct P2 = new BarcodedProduct(barcode2,
//				1500,
//				"1.5L Orange Juice.",
//				3.75);
//		BarcodeList.add(barcode2);
//		BPList.add(IP2);
//		// ItemProduct 2
//		
//		// ItemProduct 3
//		Barcode barcode3 = new Barcode(new Numeral[] {Numeral.three});
//		BarcodedProduct P3 = DummyBarcodeLookup.createItemProduct(barcode3,
//				750,
//				"Corn Flakes Cereal",
//				4);
//		BarcodeList.add(barcode3);
//		BPList.add(IP3);
//		// ItemProduct 3
//	//===============================================================================
//
//	}
//}
