package com.carlosgarguz.routeme.paths;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;

public class TravelSalesmanProblem extends AsyncTask<String, Void, Route> {

    private int initialPoint;
    private int [] rest;
    private int[][] matrix;
    private int endPoint;

    public TravelSalesmanProblem(int initialPoint, int[] rest, int[][] matrix, int endPoint) {
        this.initialPoint = initialPoint;
        this.rest = rest;
        this.matrix = matrix;
        this.endPoint = endPoint;
    }

    @Override
    protected Route doInBackground(String... strings) {
        Route route = tspAlgorithm(this.initialPoint, this.rest, this.matrix, this.endPoint);
        return route;
    }


    /****************************************************************************
     * Summary: Given a matrix of cost between some points, this method computes the best order of the points to follow to get the minimum possible cost
     * @param base: number associated to the initial point of the route
     * @param resto: an array with the number associated to each of the points except for the initial and ending ones. Example: If you go start at point 0 and
     *              end at point 5 in a route of 7 points, resto would be: int[] = {1,2,3,4,6}.
     *              If you start at 0 and end at 0, resto would be int[] = {1,2,3,4,5,6}.
     * @param matriz: matrix with the costs of going from the point of the row to the point of the column
     * @param endPoint: number associated to the end point of the route
     * @return A Route object which includes the total time of the route and the order of the points to follow.
     */


    private Route tspAlgorithm(int base, int[] resto, int[][] matriz, int endPoint) {
        //System.out.println("El resto es de tamaño: " + resto.length);


        //Creamos ruta a rellenar para devolver
        Route route = new Route();

        //String para ver en pantalla
        String sResto = "{";
        for(int i = 0; i<resto.length ; i++) {
            sResto = sResto + resto[i] + ", ";
        }
        sResto = sResto + "}";

        //Printeamos qué estamos haciendo
       // System.out.println("\n Hacemos el g(" + base + ", " + sResto);

        //Caso final
        if(resto.length == 1) {
            //System.out.println("LLegaste al caso final");
            //System.out.println("Coste " + base + resto[0] + "mas coste " + resto[0] + 0);
            //System.out.println("Coste " + base + resto[0]  + " = " + matriz[base][resto[0]] + "mas coste " + resto[0] + 0 + " = " + matriz[resto[0]][0]);

           // System.out.println("\n" + "El resultado de g(" + base + ", " + sResto + ") es: " + (matriz[base][resto[0]] + matriz[resto[0]][0]) );

            //Añadimos los últimos puntos de la ruta
            route.getOrder().add(resto[0]);
            route.getOrder().add(base);
            //Añadimos tiempo de route
            route.setTime(matriz[base][resto[0]] + matriz[resto[0]][endPoint]);

            return route;

        }else {
            //Creamos lista de rutas entre las que decidiremos la más corta
            ArrayList<Route> routes = new ArrayList<Route>();

            //recorremos el resto para calcular todas las posibles rutas
            for(int i = 0; i<resto.length; i++) {
                //Creamos nuevo resto para las llamadas recursivas del método
                boolean coincide = false; //Variable que controla no añadir al nuevo resto la base de la actual ejecución del algoritmo
                int[] nuevoResto = new int[resto.length-1];
                for(int j = 0; j<resto.length; j++) {
                    if(j!=i) {
                        if(!coincide) {
                            nuevoResto[j] = resto[j];
                        }else {
                            nuevoResto[j-1] = resto[j];
                        }
                    }else {
                        coincide = true;
                    }
                }

                //Obtenemos la ruta de la llamada recursiva
                Route auxRoute = tspAlgorithm(resto[i], nuevoResto, matriz, endPoint);
                //Sumamos el tiempo entre el destino y el resto al ya calculado en la llamada recursiva
                auxRoute.setTime(matriz[base][resto[i]] + auxRoute.getTime());
                //añadimos destino al orden de la ruta obtenida en la llamada recursiva
                auxRoute.getOrder().add(base);
                //añadimos ruta a la lista
                routes.add(auxRoute);

            }

			/*System.out.print("Los valores son: ");
			for(int i = 0; i<valores.size(); i++) {
				System.out.print(valores.get(i) + ", ");
			}*/

            //lista auxiliar para calcular la ruta más corta
            ArrayList<Integer> tiempos = new ArrayList<Integer>();

            for(int i = 0; i<routes.size(); i++) {
                tiempos.add(routes.get(i).getTime());
            }

            int minimo = Collections.min(tiempos); //Tiempo de ruta más corto
            int indiceRutaCorta = tiempos.indexOf(minimo); //Índice de la ruta corresponeite al mínimo

            //System.out.println("\n" + "El resultado de g(" + base + ", " + sResto + ") es: " + Collections.min(tiempos));

            route = routes.get(indiceRutaCorta);

            //Devuelves esa route
            return route;
        }
    }



}