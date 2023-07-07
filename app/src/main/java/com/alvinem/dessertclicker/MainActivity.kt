package com.alvinem.dessertclicker

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.alvinem.dessertclicker.data.Datasource
import com.alvinem.dessertclicker.model.Dessert
import com.alvinem.dessertclicker.ui.theme.DessertClickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        setContent {
            DessertClickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   DessertClickerApp(desserts= Datasource.dessertList)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG,"onRestart called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy Called")
    }
}

// Determine which Dessert to show

fun determineDessertToShow(
     desserts:List<Dessert>,
     dessertSold:Int
):Dessert{
    var  dessertToShow= desserts.first()
    for (dessert in desserts){
        if (dessertSold >=dessert.startProductionAmount){
            dessertToShow= dessert
        }else
        {
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            break
        }
    }
    return dessertToShow

}
@SuppressLint("StringFormatInvalid")
private fun  shareSoldDessertInformation(intentContext: Context, dessertSold: Int, revenue: Int){
    val sendIntent= Intent().apply {
        action= Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
           intentContext.getString(R.string.share_text, dessertSold, revenue)
        )
        type= "text/plain"
    }

    var shareIntent= Intent.createChooser(sendIntent,null)
    try {
        ContextCompat.startActivity(intentContext,shareIntent,null)
    }catch (e:ActivityNotFoundException){
        Toast.makeText(
            intentContext,
            intentContext.getString(R.string.sharing_not_available),
            Toast.LENGTH_LONG
        ).show()

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun DessertClickerApp(
    desserts: List<Dessert>
) {
    var revenue by rememberSaveable {mutableStateOf(0)}
    var  dessertSold by rememberSaveable {mutableStateOf(0)}
    val  currentDessertIndex by rememberSaveable {mutableStateOf(0)}
    var currentDessertPrice by rememberSaveable {
        mutableStateOf(desserts[currentDessertIndex].Price)

    }
    var currentDessertImageId by rememberSaveable {
        mutableStateOf(desserts[currentDessertIndex].imageId)

    }
Scaffold (
    topBar = {
        val intentContext= LocalContext.current
        DessertClickerAppBar(
            onShareButtonClicked={
                shareSoldDessertInformation(
                    intentContext=intentContext,
                    dessertSold=dessertSold,
                    revenue=revenue
                )
            },
            modifier= Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        )
    }
        ) { contentPadding ->
    DessertClickerScreen(
        revenue = revenue,
        dessertSold = dessertSold,
        dessertImageId = currentDessertImageId,
        onDessertClicked = {

            // Update the revenue
            revenue += currentDessertPrice
            dessertSold++

            // Show the next dessert
            val dessertToShow = determineDessertToShow(desserts, dessertSold)
            currentDessertImageId = dessertToShow.imageId
            currentDessertPrice = dessertToShow.Price
        },
        modifier = Modifier.padding(contentPadding)
    )
}
}

//fun DessertClickerScreen(
//    revenue: Int,
//    dessertSold: Int,
//    dessertImageId: Int,
//    onDessertClicked: () -> Unit,
//    modifier: Modifier) {
//
//}

@Composable
private fun DessertClickerAppBar(
    onShareButtonClicked: ()-> Unit,
    modifier: Modifier=Modifier
) {
    Row (
        modifier=modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
            )
    {
        Text(
            text = stringResource(R.string.app_name),
            modifier=Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,

            )
        IconButton(
            onClick =onShareButtonClicked,
            modifier=Modifier.padding(end = dimensionResource(R.dimen.padding_medium)),
        ){
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(R.string.share),
               tint = MaterialTheme.colorScheme.onPrimary,)

        }
    }
}

@Composable
private fun DessertClickerScreen(
    revenue: Int,
    dessertSold: Int,
    @DrawableRes dessertImageId: Int,
    onDessertClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier=modifier) {
        Image(
            painter = painterResource(R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(dessertImageId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.image_size))
                        .height(dimensionResource(R.dimen.image_size))
                        .align(Alignment.Center)
                        .clickable { onDessertClicked },
                    contentScale = ContentScale.Crop,
                )
            }
            TransactionInfo(
                revenue = revenue,
                dessertSold = dessertSold,
                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}

@Composable
private fun TransactionInfo(
    revenue: Int,
    dessertSold: Int,
    modifier: Modifier= Modifier,
) {
    Column(modifier = modifier) {
        DessertSoldInfo(
            dessertSold=dessertSold,
            modifier= Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        RevenueInfo(
            revenue=revenue,
            modifier= Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
private fun RevenueInfo(
    revenue: Int,
    modifier: Modifier=Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
       ) {
        Text(
            text = stringResource(R.string.total_revenue),
            style= MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        Text(
            text = "$${revenue}",
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private  fun DessertSoldInfo(
    dessertSold: Int,
    modifier: Modifier=Modifier,

) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.dessert_sold),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text =dessertSold.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview
@Composable
fun DessertClickerPreview() {
    DessertClickerTheme {
        DessertClickerApp(listOf(Dessert(R.drawable.cupcake,5,0)))
    }

}