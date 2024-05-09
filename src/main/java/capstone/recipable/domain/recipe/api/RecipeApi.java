package capstone.recipable.domain.recipe.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecipeApi {
    private final NaverOcrApi naverApi;
    @Value("${naver.service.secretKey}")
    private String secretKey;
    @GetMapping("/naver-ocr")
    public ResponseEntity ocr() throws IOException {
        String fileName = "second.jpg";
        File file = ResourceUtils.getFile("classpath:static/image/"+fileName);

        List<String> result = naverApi.callApi("POST", file.getPath(), secretKey, "jpg");
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
