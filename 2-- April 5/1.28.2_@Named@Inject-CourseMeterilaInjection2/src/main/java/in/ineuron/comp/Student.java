package in.ineuron.comp;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import in.ineuron.dependent.ICourseMaterial;

@Named("std")
public class Student
{
	static
	{
		System.out.println("Student.class file is loading...");
	}

	public Student()
	{
		System.out.println("Student Object is instantiated...");
	}

	// field level injection also possible
	// but constructor not possible with @Resource
	@Inject
	@Named("dotNet")
	private ICourseMaterial material;

	public void setMaterial(ICourseMaterial material)
	{
		System.out.println("Setter injection-----");
		this.material = material;
	}

	// getter for displaying injected course
	public ICourseMaterial getMaterial()
	{
		return material;
	}
	
	

}
