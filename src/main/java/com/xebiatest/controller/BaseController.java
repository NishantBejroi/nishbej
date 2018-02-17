package com.xebiatest.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xebiatest.io.XmlReader;
import com.xebiatest.model.LabYak;
import com.xebiatest.service.YakService;
import com.xebiatest.service.YakServiceImpl;
import com.xebiatest.store.YakYield;

@Controller
public class BaseController extends BaseWSController{
    private List<LabYak> yakList=new ArrayList<LabYak>();

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);
	@PostConstruct
	public void init() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("input/herd.xml").getFile());
        yakList = new XmlReader(file).read();

	}
	
	@RequestMapping(value = "/stock/{t}", method = RequestMethod.GET)
	public @org.springframework.web.bind.annotation.ResponseBody String getStock(@PathVariable Integer t, ModelMap model) {
		int elapsedTimeInDays = t;
        YakService service = new YakServiceImpl();
        for(LabYak yak : yakList){
            service.calculateAndSaveYieldForDay(yak, elapsedTimeInDays);
        }
        YakYield totalYakYield = service.getTotalYakYield(elapsedTimeInDays);
        System.out.println("T = "+elapsedTimeInDays+"\n\n");

        System.out.println("In Stock:");
        System.out.println("\t\t"+totalYakYield.getMilk()+" liters of milk");
        System.out.println("\t\t"+totalYakYield.getSkin()+" skins of wool");
        System.out.println("Herd:\n\n");
        for(LabYak yak : yakList){
            System.out.println(yak.display(elapsedTimeInDays));
        }
	
		return convertIntoJson(totalYakYield);

	}
	@RequestMapping(value = "/herd/{t}", method = RequestMethod.GET)
	public @org.springframework.web.bind.annotation.ResponseBody String getHerd(@PathVariable Integer t, ModelMap model) {
        int elapsedTimeInDays = t;
        for(LabYak yak : yakList){
        	if(yak.isAlive(elapsedTimeInDays)){
        		yak.setAge((yak.getAge()*100+elapsedTimeInDays)/100);
        		yak.setAgeAtLastShave(yak.getAgeAtLastShave()/100);
        	}
        }
   		return convertIntoJson(yakList);

	}

}