package uz.master.demotest.controller.column;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uz.master.demotest.dto.column.ColumnCreateDto;
import uz.master.demotest.dto.column.ColumnDto;
import uz.master.demotest.dto.column.ColumnUpdateDto;
import uz.master.demotest.dto.task.TaskDto;
import uz.master.demotest.services.column.ColumnService;
import uz.master.demotest.services.task.TaskService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("project/column")
public class ColumnController {
    private final ColumnService service;
private  final TaskService taskService;


    public ColumnController(
            ColumnService service,
            TaskService taskService) {
        this.service = service;
        this.taskService = taskService;
    }

    @RequestMapping("list/{id}")
    public String list(Model model, @RequestParam Long id){
        List<ColumnDto> all = service.getAll(id);

        all.forEach(idColoumn->{
          idColoumn.setTaskDtos( taskService.getAll());
        });
        model.addAttribute("columns",all);
        return "index2";
    }

    @RequestMapping(value = "create/",method = RequestMethod.POST)
    public String create(@ModelAttribute ColumnCreateDto dto){
        service.create(dto);
        return "redirect:index2";
    }
    @RequestMapping(value = "update/",method = RequestMethod.POST)
    public String create(@ModelAttribute ColumnUpdateDto dto){
        service.update(dto);
        return "redirect:index2";
    }
    @RequestMapping(value = "delete/",method = RequestMethod.DELETE)
    public String delete(@RequestParam Long id){
        service.delete(id);
        return "redirect:index2";
    }
}
