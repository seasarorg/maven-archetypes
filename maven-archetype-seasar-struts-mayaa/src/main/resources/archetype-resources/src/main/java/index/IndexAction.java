package ${package}.index;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.Property;

public class IndexAction {
	@Binding
	public IndexService indexService;

	public static final String SUCCESS = "success";

	@Property
	protected IndexDto indexDto;

	public String execute() {
		indexDto.setMessage(indexService.getMessage());
		return SUCCESS;
	}
}