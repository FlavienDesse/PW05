package fr.isen.java2.db.entities;

public class Genre {
	private Integer id;
	private String name;

	public Genre() {
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Genre){
			Genre genre = (Genre)obj;
			if(this.id.equals(genre.getId()) && this.name.equals(genre.getName())){
				return true;
			}
		}
		return false;
	}

	public Genre(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
