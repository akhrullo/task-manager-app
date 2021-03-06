package uz.master.demotest.controller.task;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.master.demotest.configs.security.UserDetails;
import uz.master.demotest.dto.column.ColumnDto;
import uz.master.demotest.dto.comment.CommentDto;
import uz.master.demotest.dto.task.TaskCreateDto;
import uz.master.demotest.dto.task.TaskDto;
import uz.master.demotest.dto.task.TaskUpdateDto;
import uz.master.demotest.entity.action.Action;
import uz.master.demotest.entity.auth.AuthUser;
import uz.master.demotest.services.column.ColumnService;
import uz.master.demotest.services.comment.CommentService;
import uz.master.demotest.services.organization.OrganizationService;
import uz.master.demotest.services.project.ProjectService;
import uz.master.demotest.services.task.TaskService;

import java.util.List;

/**
 * @author Bekpulatov Shoxruh, Thu 10:35 AM. 2/24/2022
 */

@Controller
@RequestMapping("/task/")
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;
    private final ProjectService projectService;
    private final ColumnService columnService;
    private final OrganizationService organizationService;

    public TaskController(TaskService taskService, CommentService commentService, ProjectService projectService, ColumnService columnService, OrganizationService organizationService) {
        this.taskService = taskService;
        this.commentService = commentService;
        this.projectService = projectService;
        this.columnService = columnService;
        this.organizationService = organizationService;
    }

    @GetMapping("{id}")
    public String taskPage(Model model, @PathVariable(name = "id") Long id) {
        List<Action> actions = taskService.getActions(id);
        model.addAttribute("actions", actions);

        TaskDto dto = taskService.get(id);
        model.addAttribute("dto", dto);

        List<CommentDto> comments = commentService.getAll(id);
        model.addAttribute("comments", comments);

        List<AuthUser> members = taskService.getMembers(id);
        model.addAttribute("members", members);

        ColumnDto columnDto = columnService.get(dto.getColumnId());

        List<AuthUser> projectMembers = projectService.getMembers(columnDto.getProjectId());
        model.addAttribute("projectMembers", projectMembers);

        Long orgId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getOrganization();
        model.addAttribute("organization", organizationService.getOrganization(orgId));

        return "task/task";
    }


    @GetMapping("/priority/{id}/{code}")
    public String changePriority(@PathVariable(name = "id") Long id, @PathVariable(name = "code") String code) {
        taskService.updatePriority(id, code);
        return "redirect:/task/" + id;

    }


    @GetMapping("/level/{id}/{code}")
    public String changeLevel(@PathVariable(name = "id") Long id, @PathVariable(name = "code") String code) {
        taskService.updateLevel(id, code);
        return "redirect:/task/" + id;
    }


    @GetMapping("/join/{id}")
    public String joinTask(@PathVariable(name = "id") Long id) {
        taskService.joinTask(id);
        return "redirect:/task/" + id;
    }


    @GetMapping("/create")
    public String createTaskPage(Model model, @RequestParam String colId, @RequestParam String proId) {
        model.addAttribute("id", colId);
        model.addAttribute("proId", proId);
        return "task/create";
    }


    @PostMapping("/create/{id}")
    public String createTask(@PathVariable(name = "id") Long id, TaskCreateDto dto) {

        dto.setColumnId(id);
        taskService.create(dto);
        return "redirect:/project/" + dto.getProjectId();
    }

    @GetMapping("/update/{id}")
    public String updatePage(Model model, @PathVariable(name = "id") Long id) {
        TaskDto dto = taskService.get(id);
        model.addAttribute("dto", dto);
        return "task/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable(name = "id") Long id, @ModelAttribute TaskUpdateDto dto) {
        Long projectId = taskService.getProjectId(id);
        Long teamLead = projectService.getTeamLead(projectId);
        Long userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!userId.equals(teamLead)) throw new RuntimeException("Permission Denied!!!!");
        dto.setId(id);
        taskService.update(dto);
        return "redirect:/task/" + id;
    }


    @GetMapping("/delete/{id}")
    public String deletePage(Model model, @PathVariable(name = "id") Long id) {
        TaskDto dto = taskService.get(id);
        model.addAttribute("dto", dto);
        return "task/delete";
    }


    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id) {
        Long projectId = taskService.getProjectId(id);
        Long teamLead = projectService.getTeamLead(projectId);
        Long userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!userId.equals(teamLead)) throw new RuntimeException("Permission Denied!!!!");
        taskService.delete(id);
        return "redirect:/project/" + projectId;
    }


    @GetMapping("removeMember/{taskId}/{memberId}")
    public String removeMember(@PathVariable(name = "taskId") Long taskId, @PathVariable(name = "memberId") Long memberId) {
        Long projectId = taskService.getProjectId(taskId);
        Long teamLead = projectService.getTeamLead(projectId);
        Long userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!userId.equals(teamLead)) throw new RuntimeException("Permission Denied!!!!");
        taskService.deleteMember(taskId, memberId);
        return "redirect:/task/" + taskId;
    }


    @GetMapping("addMember/{taskId}/{memberId}")
    public String addMember(@PathVariable(name = "taskId") Long taskId, @PathVariable(name = "memberId") Long memberId) {
        Long projectId = taskService.getProjectId(taskId);
        Long teamLead = projectService.getTeamLead(projectId);
        Long userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!userId.equals(teamLead)) throw new RuntimeException("Permission Denied!!!!");
        taskService.addMamber(taskId, memberId);
        return "redirect:/task/" + taskId;
    }

}
