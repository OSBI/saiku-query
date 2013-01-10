package org.saiku.query.mdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.olap4j.mdx.CallNode;
import org.olap4j.mdx.HierarchyNode;
import org.olap4j.mdx.LiteralNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.Syntax;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.metadata.Hierarchy;

public class NameFilter extends AbstractFilterFunction {

	private List<String> filterExpression = new ArrayList<String>();
	private MdxFunctionType type;
	private Hierarchy hierarchy;

	public NameFilter(Hierarchy hierarchy, String... matchingExpression) {
		List<String> expressions = Arrays.asList(matchingExpression);
		this.filterExpression.addAll(expressions);
		this.hierarchy = hierarchy;
		this.type = MdxFunctionType.Filter;
	}
	
	public NameFilter(Hierarchy hierarchy, List<String> matchingExpression) {
		this.hierarchy = hierarchy;
		this.filterExpression.addAll(matchingExpression);
		this.type = MdxFunctionType.Filter;
	}

	@Override
	public List<ParseTreeNode> getArguments(MdxParser parser) {
		List<ParseTreeNode> filters = new ArrayList<ParseTreeNode>();
		List<ParseTreeNode> arguments = new ArrayList<ParseTreeNode>();
		ParseTreeNode h =  new HierarchyNode(null, hierarchy);
		for (String filter : filterExpression) {
			ParseTreeNode filterExp =  LiteralNode.createString(null, filter);
			CallNode currentMemberNode =
					new CallNode(
							null,
							"CurrentMember",
							Syntax.Property,
							h);
			CallNode currentMemberNameNode =
					new CallNode(
							null,
							"Name",
							Syntax.Property,
							currentMemberNode);

			CallNode filterNode = 
					new CallNode(
							null,
							" = ",
							Syntax.Infix,
							currentMemberNameNode,
							filterExp);
			
			filters.add(filterNode);			
		}
		if (filters.size() == 1) {
			arguments.addAll(filters);
		} else if (filters.size() > 1) {
			ParseTreeNode allfilter = filters.get(0);
			for (int i = 1; i< filters.size(); i++) {
				allfilter = 
						new CallNode(
								null,
								" OR ",
								Syntax.Infix,
								allfilter,
								filters.get(i));
			}
			arguments.add(allfilter);
		}
		return arguments;
	}

	@Override
	public MdxFunctionType getFunctionType() {
		return type;
	}

	/**
	 * @return the filterExpression
	 */
	public List<String> getFilterExpression() {
		return filterExpression;
	}

	/**
	 * @return the hierarchy
	 */
	public Hierarchy getHierarchy() {
		return hierarchy;
	}
}
