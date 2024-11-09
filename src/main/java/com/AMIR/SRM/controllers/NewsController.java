package com.AMIR.SRM.controllers;

import com.AMIR.SRM.domain.News;
import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.repositories.NewsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping("srm/")
public class NewsController {
    @Autowired
    private NewsRepo newsRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'ADMIN')")
    @GetMapping("create_news")
    public String create_news(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Опубликовать новость");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        return "SRM/news/create_news";
    }
    @PostMapping("create_news")
    public String add(
            @RequestParam String new_title,
            @RequestParam String new_text,
            @RequestParam("file") MultipartFile file,
            Map<String, Object> model) throws IOException {

        News news = new News(new_title, new_text);
        if (!file.isEmpty()){
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resulFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resulFilename));

            news.setNew_image(resulFilename);
        }
        newsRepo.save(news);

        return "redirect:/srm/create_news?created";
    }

    @GetMapping("news_list")
    public String news_list(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Новости");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        List<News> news = newsRepo.findAll();
        Collections.sort(news, new Comparator<News>() {
            public int compare(News c1, News c2) {
                if (c1.getPub_date().after(c2.getPub_date())) return -1;
                if (c1.getPub_date().before(c2.getPub_date())) return 1;
                if (c1.getId() > (c2.getId())) return -1;
                if (c1.getId() < (c2.getId())) return -1;
                return 0;
            }});
        model.addAttribute("news", news);

        return "SRM/news/news_list";
    }

    @GetMapping("news_list/{news}")
    public String watchNew(@PathVariable News news, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Новость");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("news", news);

        return "SRM/news/watch_new";
    }

    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'ADMIN')")
    @GetMapping("news_list/redact_news/{news}")
    public String redactNews(@PathVariable News news, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Редактирование новости");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("news", news);

        return "SRM/news/redact_news";
    }

    @PostMapping("/news_list/redact_news")
    public String saveNews(
            @RequestParam String new_title,
            @RequestParam String new_text,
            @RequestParam("file") MultipartFile file,
            @RequestParam("newsId") News news,
            Map<String, Object> model
    ) throws IOException {
        news.setTitle(new_title);
        news.setText(new_text);

        if (!file.isEmpty()){
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resulFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resulFilename));

            news.setNew_image(resulFilename);
        }

        newsRepo.save(news);
        return "redirect:/srm/news_list";
    }

    @GetMapping("news_list/delete_new/{news}")
    public String deleteNew(@PathVariable News news, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Новость");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("news", news);

        newsRepo.delete(news);

        return "redirect:/srm/news_list";
    }
}
