package nl.erasmusmc.bios.euadr.signals;

public class DrugDisease {
    private String drug = null;
    private String disease = null;
    
    public DrugDisease(String drug, String disease) {
	setDrug(drug);
	setDisease(disease);
    }

    public String getDrug() {
        return drug;
    }
    
    public void setDrug(String drug) {
        this.drug = drug;
    }
    
    public String getDisease() {
        return disease;
    }
    
    public void setDisease(String disease) {
        this.disease = disease;
    }
}
