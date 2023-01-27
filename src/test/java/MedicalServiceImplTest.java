import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.spy;

public class MedicalServiceImplTest {

    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
    MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    @Test
    public void checkMassageByExtremeTemperature() {
        String id = "1";
        PatientInfo patientInfo = spy(new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("40"), new BloodPressure(125, 78))));
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);
        Mockito.when(patientInfo.getId()).thenReturn(id);
        BigDecimal extremalTemperature = new BigDecimal(38);

        medicalService.checkTemperature(id, extremalTemperature);

        Mockito.verify(sendAlertService, Mockito.only()).send(argumentCaptor.capture());
        Assertions.assertTrue(argumentCaptor.getValue().contains(id));
    }

    @Test
    public void checkMassageByExtremeBloodPressure() {
        String id = "1";
        PatientInfo patientInfo = spy(new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("40"), new BloodPressure(125, 78))));
        BloodPressure normalBloodPressure = new BloodPressure(120, 60);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patientInfo);
        Mockito.when(patientInfo.getId()).thenReturn(id);

        medicalService.checkBloodPressure(id, normalBloodPressure);

        Mockito.verify(sendAlertService, Mockito.only()).send(argumentCaptor.capture());
        Assertions.assertTrue(argumentCaptor.getValue().contains(id));
    }

    @Test
    public void checkMassageByNormalBloodPressureAndTemperature() {
        PatientInfo patientInfo = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 60)));
        Mockito.when(patientInfoRepository.getById(Mockito.anyString())).thenReturn(patientInfo);
        BloodPressure normalBloodPressure = new BloodPressure(120, 60);
        BigDecimal extremalTemperature = new BigDecimal(38);

        medicalService.checkBloodPressure(Mockito.anyString(), normalBloodPressure);
        medicalService.checkTemperature(Mockito.anyString(), extremalTemperature);

        Mockito.verify(sendAlertService, Mockito.times(0)).send(Mockito.anyString());
    }
}