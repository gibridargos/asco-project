package com.ascoproject.ascoproject.controller;

import com.ascoproject.ascoproject.entity.InfoEntity;
import com.ascoproject.ascoproject.model.group.GroupAddTaxRequest;
import com.ascoproject.ascoproject.model.group.GroupModel;
import com.ascoproject.ascoproject.model.group.GroupUpdateRequest;
import com.ascoproject.ascoproject.model.infoentity.InfoEntityModel;
import com.ascoproject.ascoproject.model.infoentity.InfoEntityUpdateModel;
import com.ascoproject.ascoproject.model.responce.ResponseAll;
import com.ascoproject.ascoproject.model.responce.ResponseResult;
import com.ascoproject.ascoproject.model.taxinfo.TaxInfoModel;
import com.ascoproject.ascoproject.model.taxinfo.TaxInfoResponse;
import com.ascoproject.ascoproject.model.taxinfo.TaxInfoUpdateModel;
import com.ascoproject.ascoproject.model.user.UserModel;
import com.ascoproject.ascoproject.model.user.UserUpdateRequest;
import com.ascoproject.ascoproject.service.FileImportService;
import com.ascoproject.ascoproject.service.InfoEntityService;
import com.ascoproject.ascoproject.service.TaxInfoService;
import com.ascoproject.ascoproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@CrossOrigin
@RequestMapping("/api/v1/bot")
@RequiredArgsConstructor
public class BotController {
    private final TaxInfoService taxInfoService;
    private final FileImportService fileImportService;
    private final InfoEntityService infoEntityService;
    private final UserService userService;

    @GetMapping("/tax-report-list/page")
    public ResponseEntity<ResponseResult<Page<TaxInfoResponse>>> getTaxReportListPaging(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
            Pageable pageable) {
        var result = "ru".equalsIgnoreCase(lang)
                ? taxInfoService.findAllRu(pageable)
                : taxInfoService.findAll(pageable);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/info-entity-list/page")
    public ResponseEntity<ResponseResult<Page<InfoEntity>>> getInfoEntityListPaging(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
            Pageable pageable) {
        var result = "ru".equalsIgnoreCase(lang)
                ? infoEntityService.findAllRu(pageable)
                : infoEntityService.findAll(pageable);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }
    @GetMapping("/tax-report/{id}")
    public ResponseEntity<ResponseResult<TaxInfoUpdateModel>> getTaxReportById(@PathVariable Long id) {
        ResponseAll<ResponseResult<TaxInfoUpdateModel>> result = taxInfoService.getTaxInfoUpdateModelById(id);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/info-entity/{id}")
    public ResponseEntity<ResponseResult<InfoEntityUpdateModel>> getInfoEntityById(@PathVariable Long id) {
        ResponseAll<ResponseResult<InfoEntityUpdateModel>> result = infoEntityService.getInfoEntityUpdateModelById(id);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/get-users/page")
    public ResponseEntity<ResponseResult<Page<UserModel>>> getUsers(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
            Pageable pageable) {
        var result = "ru".equalsIgnoreCase(lang)
                ? userService.getUsersRu(pageable)
                : userService.getUsers(pageable);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/get-groups/page")
    public ResponseEntity<ResponseResult<Page<GroupModel>>> getGroups(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
            Pageable pageable) {
        var result = userService.getGroups(pageable);

        /*        var result = "ru".equalsIgnoreCase(lang)
                ? userService.getGroupsRu(pageable)
                : userService.getGroups(pageable);*/
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/get-taxes-choose")
    public ResponseEntity<ResponseResult<List<String>>> getTaxesChoose(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang) {
        var result = "ru".equalsIgnoreCase(lang)
                ? taxInfoService.getTaxTypesRu()
                : taxInfoService.getTaxTypesUz();
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @PostMapping("/add-group-taxes/{chatId}")
    public ResponseEntity<ResponseResult<?>> addGroupTaxes(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
            @PathVariable Long chatId,
            @RequestBody GroupAddTaxRequest groupAddTaxRequest) {
        var result = "ru".equalsIgnoreCase(lang)
                ? taxInfoService.addTaxesGroupServiceRu(chatId, groupAddTaxRequest)
                : taxInfoService.addTaxesGroupServiceUz(chatId, groupAddTaxRequest);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/get-group/{chatId}")
    public ResponseEntity<ResponseResult<GroupModel>> getGroup(
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
            @PathVariable Long chatId) {
        var result = "ru".equalsIgnoreCase(lang)
                ? userService.getGroupRu(chatId)
                : userService.getGroup(chatId);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }


    @PutMapping("/group-update/{chatId}")
    public ResponseEntity<ResponseResult<?>> updateGroup(@PathVariable Long chatId, @RequestBody GroupUpdateRequest groupUpdateRequest) {
        ResponseAll<ResponseResult<String>> result = userService.updateGroup(chatId, groupUpdateRequest);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/get-user/{chatId}")
    public ResponseEntity<ResponseResult<UserModel>> getUser(@PathVariable Long chatId) {
        ResponseAll<ResponseResult<UserModel>> result = userService.getUser(chatId);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @PutMapping("/user-update/{chatId}")
    public ResponseEntity<ResponseResult<?>> updateUser(@PathVariable Long chatId, @RequestBody UserUpdateRequest userUpdateRequest) {
        ResponseAll<ResponseResult<String>> result = userService.updateUserActivity(chatId, userUpdateRequest.getIsActive());
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @PutMapping("/tax-report-update/{id}")
    public ResponseEntity<ResponseResult<String>> updateTaxReport(@PathVariable Long id, @RequestBody TaxInfoUpdateModel taxInfoUpdateModel) {
        ResponseAll<ResponseResult<String>> result = taxInfoService.updateTaxById(id, taxInfoUpdateModel.getUz(), taxInfoUpdateModel.getRu());
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @DeleteMapping("/tax-report-delete/{id}")
    public ResponseEntity<ResponseResult<?>> deleteTaxReport(@PathVariable Long id) {
        ResponseAll<ResponseResult<String>> result = taxInfoService.deleteTaxById(id);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @PutMapping("/info_entity-update/{id}")
    public ResponseEntity<ResponseResult<?>> updateInfoEntity(@PathVariable Long id, @RequestBody InfoEntityUpdateModel infoEntityUpdateModel) {
        ResponseAll<ResponseResult<String>> result = infoEntityService.updateInfoById(id, infoEntityUpdateModel.getUz(), infoEntityUpdateModel.getRu());
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @DeleteMapping("/info-entity-delete/{id}")
    public ResponseEntity<ResponseResult<?>> deleteInfoEntity(@PathVariable Long id) {
        ResponseAll<ResponseResult<String>> result = infoEntityService.deleteInfoEntityById(id);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @PostMapping("/add-info_entity")
    public ResponseEntity<ResponseResult<?>> addInfoEntity(@RequestBody InfoEntityUpdateModel infoEntityUpdateModel) {
        ResponseAll<ResponseResult<String>> result = infoEntityService.addInfoEntity(infoEntityUpdateModel.getUz(), infoEntityUpdateModel.getRu());
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @PostMapping("/add-tax-report")
    public ResponseEntity<ResponseResult<?>> addTaxReport(@RequestBody TaxInfoUpdateModel taxInfoUpdateModel) {
        ResponseAll<ResponseResult<String>> result = taxInfoService.addTaxReport(taxInfoUpdateModel.getUz(), taxInfoUpdateModel.getRu());
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @Operation(summary = "Import CSV file", description = "Upload tax info from a CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded")
    })
    @PostMapping(value = "/import-tax-info-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseResult<?>> uploadTaxInfoCsv(
            @Parameter(description = "CSV file", required = true)
            @RequestParam("file") MultipartFile file) throws Exception {
        ResponseAll<ResponseResult<String>> result = fileImportService.importCsv(file);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }


    @GetMapping("/export-tax-info-csv")
    public ResponseEntity<ResponseResult<?>> exportTaxInfoToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"tax_info.csv\"");
        ResponseAll<ResponseResult<String>> result = fileImportService.exportTaxReportCsv(response);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @Operation(summary = "Import CSV file", description = "Upload info entity from a CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded")
    })
    @PostMapping(value = "/import-info-entity-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseResult<?>> uploadInfoEntityCsv(@Parameter(description = "CSV file", required = true)
                                                                 @RequestParam("file") MultipartFile file) throws Exception {
        ResponseAll<ResponseResult<String>> result = fileImportService.importInfoEntityCsv(file);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

    @GetMapping("/export-info-entity-csv")
    public ResponseEntity<ResponseResult<?>> exportInfoEntityToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"info_entity.csv\"");
        ResponseAll<ResponseResult<String>> result = fileImportService.exportInfoEntityCsv(response);
        return ResponseEntity.status(result.getStatus()).body(result.getResponse());
    }

}