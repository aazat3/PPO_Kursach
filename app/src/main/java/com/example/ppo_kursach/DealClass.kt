package com.example.ppo_kursach

data class DealClass(
    val date:String,
    val address:String,
    val user:String = "s",
    val client:String = "a"
):java.io.Serializable{
    companion object {
        fun getDealData():ArrayList<DealClass>{
            // create an arraylist of type employee class
            val employeeList=ArrayList<DealClass>()
            val emp1=DealClass("Chinmaya Mohapatra","chinmaya@gmail.com")
            employeeList.add(emp1)
            val emp2=DealClass("Ram prakash","ramp@gmail.com")
            employeeList.add(emp2)
            val emp3=DealClass("OMM Meheta","mehetaom@gmail.com")
            employeeList.add(emp3)
            val emp4=DealClass("Hari Mohapatra","harim@gmail.com")
            employeeList.add(emp4)
            val emp5=DealClass("Abhisek Mishra","mishraabhi@gmail.com")
            employeeList.add(emp5)
            val emp6=DealClass("Sindhu Malhotra","sindhu@gmail.com")
            employeeList.add(emp6)
            val emp7=DealClass("Anil sidhu","sidhuanil@gmail.com")
            employeeList.add(emp7)
            val emp8=DealClass("Sachin sinha","sinhas@gmail.com")
            employeeList.add(emp8)
            val emp9=DealClass("Amit sahoo","sahooamit@gmail.com")
            employeeList.add(emp9)
            val emp10=DealClass("Raj kumar","kumarraj@gmail.com")
            employeeList.add(emp10)

            return  employeeList
        }
    }
}