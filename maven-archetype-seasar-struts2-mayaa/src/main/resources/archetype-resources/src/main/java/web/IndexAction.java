package ${package}.web;

import com.opensymphony.xwork2.ActionSupport;

import org.seasar.framework.container.annotation.tiger.Binding;

public class IndexAction extends ActionSupport {
	@Binding
	public IndexService indexService;

	public String execute() {
		return SUCCESS;
	}

	public String getMessage() {
		return indexService.getMessage();
	}
}
