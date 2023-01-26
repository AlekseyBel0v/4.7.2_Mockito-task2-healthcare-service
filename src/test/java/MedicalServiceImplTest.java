import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    String id1 = "1";
    PatientInfo patientInfo1 = spy(new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 60))));
    String id2 = "2";
    PatientInfo patientInfo2 = spy(new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
            new HealthInfo(new BigDecimal("40"), new BloodPressure(125, 78))));

    @BeforeEach
    void setMocksProperties() {
        Mockito.when(patientInfoRepository.getById(id1)).thenReturn(patientInfo1);
        Mockito.when(patientInfoRepository.getById(id2)).thenReturn(patientInfo2);
        Mockito.when(patientInfo1.getId()).thenReturn(id1);
        Mockito.when(patientInfo2.getId()).thenReturn(id2);
    }

    @Test
    public void checkMassageByTemperature() {
        //сообщение не должно отправляться для пациента 1, но должно быть отправлено для пациента 2
        BigDecimal extremalTemperature = new BigDecimal(38);

        medicalService.checkTemperature(id1, extremalTemperature);
        medicalService.checkTemperature(id2, extremalTemperature);

        Mockito.verify(sendAlertService, Mockito.only()).send(argumentCaptor.capture());
        Assertions.assertTrue(argumentCaptor.getValue().contains(id2));
    }

    @Test
    public void checkMassageByBloodPressure() {
        //сообщение не должно отправляться для пациента 1, но должно быть отправлено для пациента 2
        BloodPressure normalBloodPressure = new BloodPressure(120, 60);

        medicalService.checkBloodPressure(id1, normalBloodPressure);
        medicalService.checkBloodPressure(id2, normalBloodPressure);

        Mockito.verify(sendAlertService, Mockito.only()).send(argumentCaptor.capture());
        Assertions.assertTrue(argumentCaptor.getValue().contains(id2));
    }
}