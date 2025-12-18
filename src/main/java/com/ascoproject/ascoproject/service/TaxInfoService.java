package com.ascoproject.ascoproject.service;

import com.ascoproject.ascoproject.entity.InfoEntity;
import com.ascoproject.ascoproject.entity.TaxInfoEntity;
import com.ascoproject.ascoproject.model.group.GroupAddTaxRequest;
import com.ascoproject.ascoproject.model.group.TaxTypes;
import com.ascoproject.ascoproject.model.infoentity.InfoEntityModel;
import com.ascoproject.ascoproject.model.responce.ResponseAll;
import com.ascoproject.ascoproject.model.responce.ResponseResult;
import com.ascoproject.ascoproject.model.taxinfo.TaxInfoModel;
import com.ascoproject.ascoproject.model.taxinfo.TaxInfoResponse;
import com.ascoproject.ascoproject.model.taxinfo.TaxInfoUpdateModel;
import com.ascoproject.ascoproject.repository.TaxInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxInfoService {
    private final TaxInfoRepository taxInfoRepository;
    private final TranslateService translateService;
    private final UserService userService;


    public synchronized TaxInfoEntity save(TaxInfoEntity taxInfoEntity) {
        return taxInfoRepository.save(taxInfoEntity);
    }

    public synchronized void saveAll(List<TaxInfoEntity> taxInfoEntities) {
        taxInfoRepository.saveAll(taxInfoEntities);
    }

    public synchronized List<TaxInfoEntity> findAll() {
        return taxInfoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public synchronized ResponseAll<ResponseResult<Page<TaxInfoResponse>>> findAll(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("id").ascending()
            );
        }
        List<TaxInfoResponse> collect = taxInfoRepository.findAllTaxInfo().stream()
                .filter(t -> t.getTaxType() != null && !t.isDeleted())
                .collect(Collectors.groupingBy(
                        TaxInfoEntity::getTaxType,
                        LinkedHashMap::new, // tartibni saqlash uchun!
                        Collectors.mapping(this::mapToModel, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> {
                    TaxInfoResponse response = new TaxInfoResponse();
                    response.setTaxType(entry.getKey());
                    response.setTaxesByType(entry.getValue());
                    return response;
                })
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), collect.size());
        List<TaxInfoResponse> pageContent = collect.subList(start, end);
        ResponseResult<Page<TaxInfoResponse>> responseResult = new ResponseResult<>();
        responseResult.setResult(new PageImpl<>(pageContent, pageable, collect.size()));
        ResponseAll<ResponseResult<Page<TaxInfoResponse>>> responseAll = new ResponseAll<>();
        responseAll.setResponse(responseResult);
        responseAll.setStatus(200);
        return responseAll;
    }

    private TaxInfoModel mapToModel(TaxInfoEntity entity) {
        TaxInfoModel model = new TaxInfoModel();
        model.setId(entity.getId());
        model.setTaxType(entity.getTaxType());
        model.setReportName(entity.getReportName());
        model.setReportingPeriod(entity.getReportingPeriod());
        model.setReportDate(entity.getReportDate());
        model.setReportComment(entity.getReportComment());
        model.setTaxName(entity.getTaxName());
        model.setPaymentPeriod(entity.getPaymentPeriod());
        model.setTaxDate(entity.getTaxDate());
        model.setTaxComment(entity.getTaxComment());
        model.setDeleted(entity.isDeleted());
        return model;
    }

    private TaxInfoModel mapToTranslateModel(TaxInfoEntity entity) {
        TaxInfoModel model = new TaxInfoModel();
        model.setId(entity.getId());
        model.setTaxType(translateSafe(entity.getTaxType()));
        model.setReportName(translateSafe(entity.getReportName()));
        model.setReportingPeriod(translateSafe(entity.getReportingPeriod()));
        model.setReportDate(entity.getReportDate());
        model.setReportComment(translateSafe(entity.getReportComment()));
        model.setTaxName(translateSafe(entity.getTaxName()));
        model.setPaymentPeriod(translateSafe(entity.getPaymentPeriod()));
        model.setTaxDate(entity.getTaxDate());
        model.setTaxComment(translateSafe(entity.getTaxComment()));
        model.setDeleted(entity.isDeleted());
        return model;
    }

    public ResponseAll<ResponseResult<List<String>>> getTaxTypesUz() {
        ResponseResult<List<String>> result = new ResponseResult<>();

        result.setResult(taxInfoRepository.findAllTaxInfo().stream()
                .map(TaxInfoEntity::getTaxType)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new)) // tartibni saqlaydi
                .stream()
                .toList());
        return ResponseAll.<ResponseResult<List<String>>>builder()
                .response(result)
                .status(200)
                .build();
    }

    public ResponseAll<ResponseResult<List<String>>> getTaxTypesRu() {
        List<String> taxTypes = taxInfoRepository.findAllTaxTypes();
        List<String> taxTypesRu = new ArrayList<>();

        for(String taxType: taxTypes) {
            taxTypesRu.add(translateSafe(taxType));
        }

        ResponseResult<List<String>> result = new ResponseResult<>();
        result.setResult(taxTypesRu);



        return ResponseAll.<ResponseResult<List<String>>>builder()
                .response(result)
                .status(200)
                .build();
    }


    @Transactional(readOnly = true)
    public ResponseAll<ResponseResult<Page<TaxInfoResponse>>> findAllRu(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("id").ascending()
            );
        }

        List<TaxInfoResponse> collect = taxInfoRepository.findAllTaxInfo().stream()
                .filter(t -> t.getTaxType() != null && !t.isDeleted())
                .collect(Collectors.groupingBy(
                        TaxInfoEntity::getTaxType,
                        LinkedHashMap::new, // tartibni saqlash uchun!
                        Collectors.mapping(this::mapToTranslateModel, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> {
                    TaxInfoResponse response = new TaxInfoResponse();
                    response.setTaxType(translateSafe(entry.getKey()));
                    response.setTaxesByType(entry.getValue());
                    return response;
                })
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), collect.size());
        List<TaxInfoResponse> pageContent = collect.subList(start, end);
        ResponseResult<Page<TaxInfoResponse>> responseResult = new ResponseResult<>();
        responseResult.setResult(new PageImpl<>(pageContent, pageable, collect.size()));
        ResponseAll<ResponseResult<Page<TaxInfoResponse>>> responseAll = new ResponseAll<>();
        responseAll.setResponse(responseResult);
        responseAll.setStatus(200);
        return responseAll;
    }

    private String translateSafe(String text) {
        return text == null ? null : translateService.translate(text);
    }

/*
    @Transactional(readOnly = true)
    public synchronized Page<TaxInfoEntity> findAllRu(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("id").ascending()
            );
        }
        Page<TaxInfoEntity> originalPage = taxInfoRepository.findAll(pageable);

        List<TaxInfoEntity> translatedList = originalPage.getContent().stream()
                .map(taxInfoEntity -> TaxInfoEntity.builder()
                        .id(taxInfoEntity.getId())
                        .taxType(translateService.translate(taxInfoEntity.getTaxType()))
                        .reportName(translateService.translate(taxInfoEntity.getReportName()))
                        .reportingPeriod(translateService.translate(taxInfoEntity.getReportingPeriod()))
                        .reportDate(taxInfoEntity.getReportDate())
                        .reportComment(translateService.translate(taxInfoEntity.getReportComment()))
                        .taxName(translateService.translate(taxInfoEntity.getTaxName()))
                        .paymentPeriod(translateService.translate(taxInfoEntity.getPaymentPeriod()))
                        .taxDate(taxInfoEntity.getTaxDate())
                        .taxComment(translateService.translate(taxInfoEntity.getTaxComment()))
                        .deleted(taxInfoEntity.isDeleted())
                        .build())
                .toList();

        return new PageImpl<>(translatedList, pageable, originalPage.getTotalElements());
    }
*/


    public synchronized TaxInfoEntity findTaxInfoEntityById(Long id) {
        return taxInfoRepository.findTaxInfoEntityById(id);
    }

    public synchronized ResponseAll<ResponseResult<String>> updateTaxById(Long id, TaxInfoModel uz, TaxInfoModel ru) {
        TaxInfoEntity taxInfoEntityById = findTaxInfoEntityById(id);
        taxInfoEntityById.setTaxType(uz.getTaxType());
        taxInfoEntityById.setReportName(uz.getReportName());
        taxInfoEntityById.setReportingPeriod(uz.getReportingPeriod());
        taxInfoEntityById.setReportDate(uz.getReportDate());
        taxInfoEntityById.setReportComment(uz.getReportComment());
        taxInfoEntityById.setTaxName(uz.getTaxName());
        taxInfoEntityById.setPaymentPeriod(uz.getPaymentPeriod());
        taxInfoEntityById.setTaxDate(uz.getTaxDate());
        taxInfoEntityById.setTaxComment(uz.getTaxComment());
        taxInfoEntityById.setDeleted(uz.isDeleted());
        taxInfoRepository.save(taxInfoEntityById);
        translateService.updateTranslated(uz, ru);
        ResponseResult<String> result = new ResponseResult<>();
        result.setResult("update successfully");
        return ResponseAll.<ResponseResult<String>>builder()
                .response(result)
                .status(200)
                .build();
    }

    public synchronized void deleteAllTaxInfo() {
        userService.deleteAllTaxInfo();
        taxInfoRepository.deleteAll();
        taxInfoRepository.truncateTaxInfo();
    }

    public synchronized ResponseAll<ResponseResult<String>> deleteTaxById(Long id) {
        userService.deleteTaxInfo(id);
        taxInfoRepository.deleteTaxInfo(id);
        ResponseResult<String> result = new ResponseResult<>();
        result.setResult("delete successfully");
        return ResponseAll.<ResponseResult<String>>builder()
                .response(result)
                .status(200)
                .build();
    }

    public synchronized void truncateTaxInfo() {
        taxInfoRepository.truncateTaxInfo();
    }

    public ResponseAll<ResponseResult<String>> addTaxReport(TaxInfoModel uz, TaxInfoModel ru) {
        TaxInfoEntity taxInfoEntity = new TaxInfoEntity();
        taxInfoEntity.setTaxType(uz.getTaxType());
        taxInfoEntity.setReportName(uz.getReportName());
        taxInfoEntity.setReportingPeriod(uz.getReportingPeriod());
        taxInfoEntity.setReportDate(uz.getReportDate());
        taxInfoEntity.setReportComment(uz.getReportComment());
        taxInfoEntity.setTaxName(uz.getTaxName());
        taxInfoEntity.setPaymentPeriod(uz.getPaymentPeriod());
        taxInfoEntity.setTaxDate(uz.getTaxDate());
        taxInfoEntity.setTaxComment(uz.getTaxComment());
        taxInfoEntity.setDeleted(false);
        taxInfoRepository.save(taxInfoEntity);
        translateService.updateTranslated(uz, ru);
        ResponseResult<String> result = new ResponseResult<>();
        result.setResult("tax_info successfully added");
        return ResponseAll.<ResponseResult<String>>builder()
                .response(result)
                .status(200)
                .build();
    }


    public synchronized List<TaxInfoEntity> getTaxInfoByType(String typeTax) {
        return taxInfoRepository.findAllByTaxType(typeTax);
    }

    public synchronized ResponseAll<ResponseResult<String>> addTaxesGroupServiceUz(Long chatId, GroupAddTaxRequest groupAddTaxRequest) {
        List<TaxInfoEntity> taxInfoEntityList = new LinkedList<>();
        for (TaxTypes taxType : groupAddTaxRequest.getTaxTypes()) {
            if (taxType.getChecked()) {
                taxInfoEntityList.addAll(getTaxInfoByType(taxType.getTaxType()));
            }
        }
        userService.addTaxesGroup(chatId, taxInfoEntityList);
        ResponseResult<String> result = new ResponseResult<>();
        result.setResult("add taxes successfully");
        return ResponseAll.<ResponseResult<String>>builder()
                .response(result)
                .status(200)
                .build();
    }

    public synchronized ResponseAll<ResponseResult<String>> addTaxesGroupServiceRu(Long chatId, GroupAddTaxRequest groupAddTaxRequest) {
        List<TaxInfoEntity> taxInfoEntityList = new LinkedList<>();
        for (TaxTypes taxType : groupAddTaxRequest.getTaxTypes()) {
            if (taxType.getChecked()) {
                taxInfoEntityList.addAll(getTaxInfoByType(translateService.translateRuToUz(taxType.getTaxType())));
            }
        }
        userService.addTaxesGroup(chatId, taxInfoEntityList);
        ResponseResult<String> result = new ResponseResult<>();
        result.setResult("add taxes successfully");
        return ResponseAll.<ResponseResult<String>>builder()
                .response(result)
                .status(200)
                .build();
    }

    public TaxInfoModel getTaxInfoByIdRu(Long id) {
        TaxInfoEntity info = taxInfoRepository.findById(id).orElse(null);
        assert info != null;
        return TaxInfoModel.builder()
                .id(id)
                .taxType(translateService.translate(info.getTaxType()))
                .reportName(translateService.translate(info.getReportName()))
                .reportingPeriod(translateService.translate(info.getReportingPeriod()))
                .reportDate(translateService.translate(info.getReportDate()))
                .reportComment(translateService.translate(info.getReportComment()))
                .taxName(translateService.translate(info.getTaxName()))
                .paymentPeriod(translateService.translate(info.getPaymentPeriod()))
                .taxDate(translateService.translate(info.getTaxDate()))
                .taxComment(translateService.translate(info.getTaxComment()))
                .deleted(info.isDeleted())
                .build();
    }

    public TaxInfoModel getTaxInfoById(Long id) {
        TaxInfoEntity info = taxInfoRepository.findById(id).orElse(null);
        assert info != null;
        return TaxInfoModel.builder()
                .id(id)
                .taxType(info.getTaxType())
                .reportName(info.getReportName())
                .reportingPeriod(info.getReportingPeriod())
                .reportDate(info.getReportDate())
                .reportComment(info.getReportComment())
                .taxName(info.getTaxName())
                .paymentPeriod(info.getPaymentPeriod())
                .taxDate(info.getTaxDate())
                .taxComment(info.getTaxComment())
                .deleted(info.isDeleted())
                .build();
    }

    public ResponseAll<ResponseResult<TaxInfoUpdateModel>> getTaxInfoUpdateModelById(Long id) {
        ResponseResult<TaxInfoUpdateModel> result = ResponseResult.<TaxInfoUpdateModel>builder()
                .result(TaxInfoUpdateModel.builder()
                        .uz(getTaxInfoById(id))
                        .ru(getTaxInfoByIdRu(id))
                        .build())
                .build();
        return ResponseAll.<ResponseResult<TaxInfoUpdateModel>>builder()
                .response(result)
                .status(200)
                .build();
    }

}
