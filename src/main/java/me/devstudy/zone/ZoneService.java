package me.devstudy.zone;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.dto.ZoneForm;
import me.devstudy.account.domain.Account;
import me.devstudy.zone.domain.AccountZone;
import me.devstudy.zone.domain.Zone;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final AccountZoneRepository accountZoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<String> allLines = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);
            List<Zone> zones = allLines.stream().map(Zone::map).collect(Collectors.toList());
            zoneRepository.saveAll(zones);
        }
    }

    public List<String> getZones(Account account) {
        return Optional.ofNullable(accountZoneRepository.findByAccount(account))
                .orElse(Collections.emptySet())
                .stream()
                .map(AccountZone::getZone)
                .map(Zone::toString)
                .collect(Collectors.toList());
    }

    public List<String> getAllZones() {
        return zoneRepository.findAll()
                .stream()
                .map(Zone::toString)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addZone(Account account, ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
                .orElseThrow(IllegalArgumentException::new);
        AccountZone accountZone = AccountZone.createAccountZone(account, zone);
        accountZoneRepository.save(accountZone);
    }

    @Transactional
    public void removeZone(Account account, ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName())
                .orElseThrow(IllegalArgumentException::new);
        AccountZone accountZone = accountZoneRepository.findByAccountAndZone(account, zone);
        accountZoneRepository.delete(accountZone);
    }
}
